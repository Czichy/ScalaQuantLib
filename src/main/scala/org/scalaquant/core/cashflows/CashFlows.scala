package org.scalaquant.core.cashflows

import java.time.LocalDate

import org.scalaquant.core.cashflows.coupons.Coupon
import org.scalaquant.core.common.{Event, InterestRate}
import org.scalaquant.core.common.time.JodaDateTimeHelper
import org.scalaquant.core.termstructures.YieldTermStructure
import org.scalaquant.core.types._

object CashFlows {

  type Leg = Seq[CashFlow]

  type CashFlowFunction[T] = (LocalDate, Boolean) => T
  type CashFlowResult[R] = CashFlowFunction[R] => R

  type CouponFunction[T] = CashFlowFunction[T]
  type CouponResult[R] = CashFlowFunction[R]

  type YieldTermStructureFunction = (YieldTermStructure, LocalDate, Boolean, LocalDate) => Rate
  type IRRFunction = (InterestRate, LocalDate, Boolean, LocalDate) => Rate

  private def occurredAt(date: LocalDate, includeDate: Boolean) = Event.hasOccurred(_, date, includeDate)

//  private val occurredCashFlow: CashFlow => CashFlowFunction[Boolean] =
//    cashFlow => (date, include) => Event.hasOccurred(cashFlow.date, date, include) && cashFlow.(date)
//

  trait DateInspectors {
    protected def leg: Leg

    private val couponOrCashflow: PartialFunction[CashFlow, LocalDate] = {
      case c: Coupon => c.accrualStartDate
      case cf: CashFlow => cf.date
    }

    def startDate: LocalDate = {
      require(leg.nonEmpty, "empty leg")

      leg.collect(couponOrCashflow).fold(JodaDateTimeHelper.farFuture)(JodaDateTimeHelper.min)
    }

    def maturityDate: LocalDate = {
      require(leg.nonEmpty, "empty leg")

      leg.collect(couponOrCashflow).fold(JodaDateTimeHelper.theBeginningOfTime)(JodaDateTimeHelper.max)
    }

    def isExpired(settlementDate: LocalDate, includeSettlementDateFlows: Boolean): Boolean = {
      def hasOccurred = occurredAt(settlementDate, includeSettlementDateFlows)

      if (leg.isEmpty) {
        true
      } else {
        leg.forall(hasOccurred)
      }
    }

    def isTradeableAt(settlementDate: LocalDate, includeSettlementDateFlows: Boolean): Boolean = {
      !isExpired(settlementDate, includeSettlementDateFlows)
    }

  }

  trait CashFlowFunctions {
    protected def leg: Leg


    val previousCashFlow: CashFlowFunction[Option[CashFlow]] = (date, include) => leg.reverse.find(occurredAt(date, include))
    val nextCashFlow: CashFlowFunction[Option[CashFlow]] = (date, include) => leg.find(!occurredAt(date, include)(_))

    val previousCashFlowDate: CashFlowFunction[Option[LocalDate]] = previousCashFlow(_, _).map(_.date)
    val nextCashFlowDate: CashFlowFunction[Option[LocalDate]] = nextCashFlow(_, _).map(_.date)

    val previousCashFlowAmount: CashFlowFunction[Double] = previousCashFlow(_, _).map(_.amount).sum
    val nextCashFlowAmount: CashFlowFunction[Double] = nextCashFlow(_, _).map(_.amount).sum

  }

  trait CouponFunctions extends CashFlowFunctions {


    private val couponRate: PartialFunction[CashFlow, Double] = { case x: Coupon => x.rate }
    private val couponNominal: PartialFunction[CashFlow, Double] = { case x: Coupon => x.nominal }

    val previousCouponRate: CouponFunction[Double] = previousCashFlow(_,_).collect(couponRate).sum
    val nextCouponRate: CouponFunction[Double] = nextCashFlow(_,_).collect(couponRate).sum

    val nominal: CouponFunction[Double] = nextCashFlow(_,_).collect(couponNominal).sum

    val accrualStartDate: CouponFunction[Option[LocalDate]] = nextCashFlow(_,_).collect{ case x: Coupon => x.accrualStartDate }
    val accrualEndDate: CouponFunction[Option[LocalDate]] = nextCashFlow(_,_).collect{ case x: Coupon => x.accrualEndDate }

    val accrualPeriod: CouponFunction[YearFraction] = nextCashFlow(_,_).collect{ case x: Coupon => x.accrualPeriod }.sum
    val accruedPeriod: CouponFunction[YearFraction] = nextCashFlow(_,_).collect{ case x: Coupon => x.accruedPeriod(x.date) }.sum

    val accrualDays: CouponFunction[Int] = nextCashFlow(_,_).collect{ case x: Coupon => x.accrualDays }.sum
    val accruedDays: CouponFunction[Long] = nextCashFlow(_,_).collect{ case x: Coupon => x.accruedDays(x.date) }.sum
    val accruedAmount: CouponFunction[Double] = nextCashFlow(_,_).collect{ case x: Coupon => x.accruedAmount(x.date) }.sum

  }

  trait YieldTermStructureFunctions {

    protected def leg: Leg

    private val basisPoint = 1.0e-4
    private val npvSum = (_: Leg).map(cashFlow => cashFlow.amount * (_: YieldTermStructure).discount(cashFlow.date)).sum
    private val bpsSum = (_: Leg).collect{ case cp: Coupon => cp.nominal * cp.accrualPeriod * (_: YieldTermStructure).discount(cp.date)}.sum

    val npv: YieldTermStructureFunction = (discountCurve, settlementDate, includeSettlementDateFlows, npvDate) => {

      val filteredLeg = leg.filterNot(occurredCashFlow(_)(settlementDate, includeSettlementDateFlows))

      npvSum(filteredLeg)(discountCurve) / discountCurve.discount(npvDate)
    }

    val bps: YieldTermStructureFunction = (discountCurve, settlementDate, includeSettlementDateFlows, npvDate) => {

      val filteredLeg = leg.filterNot(occurredCashFlow(_)(settlementDate, includeSettlementDateFlows))

      basisPoint * bpsSum(filteredLeg)(discountCurve) / discountCurve.discount(npvDate)
    }

    def atmRate(discountCurve: YieldTermStructure,
                includeSettlementDateFlows: Boolean,
                settlementDate: LocalDate,
                npvDate: LocalDate, targetNpv: Option[Rate]): Rate = {

      val bpsValue = bps(discountCurve,settlementDate,includeSettlementDateFlows, npvDate)
      val npvValue = npv(discountCurve,settlementDate,includeSettlementDateFlows, npvDate)

      require(bpsValue != 0.0, "null bps: impossible atm rate")

      targetNpv.map( x=> (x * discountCurve.discount(npvDate) - npvValue) / bpsValue).getOrElse(0.0)

    }

  }
   trait IRRFunctions {

     protected def leg: Leg


     def npv(y: InterestRate,
            includeSettlementDateFlows: Boolean,
            settlementDate: LocalDate,
            npvDate: LocalDate): Rate = {

       def fractions(amount:Double, couponDate:LocalDate, refStart:Option[LocalDate], refEnd:Option[LocalDate]) = {
           val t = y.dc.fractionOfYear(npvDate, couponDate, refStart, refEnd)
           val b = y.discountFactor(t)
           val p = amount * b
           val dPdy = t * amount * b
           (p, dPdy)
         }

       def PdPdy: PartialFunction[CashFlow, (Double, Double)] = {
         case c: Coupon =>
           val refStartDate = c.refPeriodStart
           val refEndDate = c.refPeriodEnd
           fractions(c.amount, c.date,refStartDate, refEndDate)
         case CashFlow(amount, date) =>
           val refStartDate = Some(date.plusYears(-1))
           val refEndDate = Some(date)
           fractions(amount, date, refStartDate, refEndDate)
        }

      leg match {
        case Nil => 0.0
        case cashflows =>
          val filteredLeg = cashflows.filterNot(occurredCashFlow(_)(settlementDate, includeSettlementDateFlows))
          val (p, dPdy) = filteredLeg.collect(PdPdy).reduce((left, right) => (left._1 + right._1, left._2 + right._2))
          p / dPdy
      }


    }

    def bps(y: InterestRate,
            includeSettlementDateFlows:Boolean,
            settlementDate: LocalDate,
            npvDate: LocalDate) = {


      leg match {
        case Nil => 0.0
        case cashflows =>
          val filteredLeg = cashflows.filterNot(occurredCashFlow(_)(settlementDate, includeSettlementDateFlows))
          val (p, dPdy) = filteredLeg.collect(PdPdy).reduce((left, right) => (left._1 + right._1, left._2 + right._2))
          p / dPdy
      }

      FlatForward.flatRate(settlementDate, y.rate, y.dayCounter,
      y.compounding, yield.frequency);
      return bps(leg, flatRate,
        includeSettlementDateFlows,
        settlementDate, npvDate);

    }
//
//    //! Implied internal rate of return.
//    /*! The function verifies
//        the theoretical existance of an IRR and numerically
//        establishes the IRR to the desired precision.
//    */
//    def irrAmount(
//      Real npv,
//      const DayCounter& dayCounter,
//      Compounding compounding,
//      Frequency frequency,
//      bool includeSettlementDateFlows,
//      Date settlementDate = Date(),
//      Date npvDate = Date(),
//      Real accuracy = 1.0e-10,
//      Size maxIterations = 100,
//      Rate guess = 0.05);
//
//
//    def duration(const Leg& leg,
//      const InterestRate& yield,
//    Duration::Type type,
//    bool includeSettlementDateFlows,
//    Date settlementDate = Date(),
//    Date npvDate = Date());
//    static Time duration(const Leg& leg,
//      Rate yield,
//    const DayCounter& dayCounter,
//    Compounding compounding,
//    Frequency frequency,
//    Duration::Type type,
//    bool includeSettlementDateFlows,
//    Date settlementDate = Date(),
//    Date npvDate = Date());
//
//
//    def convexity(const Leg& leg,
//      const InterestRate& yield,
//    bool includeSettlementDateFlows,
//    Date settlementDate = Date(),
//    Date npvDate = Date());
//    static Real convexity(const Leg& leg,
//      Rate yield,
//    const DayCounter& dayCounter,
//    Compounding compounding,
//    Frequency frequency,
//    bool includeSettlementDateFlows,
//    Date settlementDate = Date(),
//    Date npvDate = Date());
//
//
//    def basisPointValue(const Leg& leg,
//      const InterestRate& yield,
//    bool includeSettlementDateFlows,
//    Date settlementDate = Date(),
//    Date npvDate = Date());
//    static Real basisPointValue(const Leg& leg,
//      Rate yield,
//    const DayCounter& dayCounter,
//    Compounding compounding,
//    Frequency frequency,
//    bool includeSettlementDateFlows,
//    Date settlementDate = Date(),
//    Date npvDate = Date());
//
//   def yieldValueBasisPoint(const Leg& leg,
//      const InterestRate& yield,
//    bool includeSettlementDateFlows,
//    Date settlementDate = Date(),
//    Date npvDate = Date());
//    static Real yieldValueBasisPoint(const Leg& leg,
//      Rate yield,
//    const DayCounter& dayCounter,
//    Compounding compounding,
//    Frequency frequency,
//    bool includeSettlementDateFlows,
//    Date settlementDate = Date(),
//    Date npvDate = Date());
//    //@}
//
//    //! \name Z-spread functions
//    /*! For details on z-spread refer to:
//        "Credit Spreads Explained", Lehman Brothers European Fixed
//        Income Research - March 2004, D. O'Kane
//    */
//    //@{
//    //! NPV of the cash flows.
//    /*! The NPV is the sum of the cash flows, each discounted
//        according to the z-spreaded term structure.  The result
//        is affected by the choice of the z-spread compounding
//        and the relative frequency and day counter.
//    */
//    def npv(const Leg& leg,
//      const boost::shared_ptr<YieldTermStructure>& discount,
//      Spread zSpread,
//      const DayCounter& dayCounter,
//      Compounding compounding,
//      Frequency frequency,
//      bool includeSettlementDateFlows,
//      Date settlementDate = Date(),
//      Date npvDate = Date());
//    //! implied Z-spread.
//    def zSpread(const Leg& leg,
//      Real npv,
//      const boost::shared_ptr<YieldTermStructure>&,
//      const DayCounter& dayCounter,
//      Compounding compounding,
//      Frequency frequency,
//      bool includeSettlementDateFlows,
//      Date settlementDate = Date(),
//      Date npvDate = Date(),
//      Real accuracy = 1.0e-10,
//      Size maxIterations = 100,
//      Rate guess = 0.0);
  }
}
