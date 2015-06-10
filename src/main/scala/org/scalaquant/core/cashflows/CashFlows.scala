package org.scalaquant.core.cashflows

import org.joda.time.LocalDate
import org.scalaquant.core.cashflows._
import org.scalaquant.core.cashflows.coupons.Coupon
import org.scalaquant.core.common.InterestRate
import org.scalaquant.core.common.time.JodaDateTimeHelper
import org.scalaquant.core.termstructures.YieldTermStructure

object CashFlows {

  type CashFlowFunction[T] = (Leg, Boolean, LocalDate) => T

  def startDate(leg: Leg): LocalDate = {
    require(leg.nonEmpty, "empty leg")

    leg.collect{
        case c: Coupon => c.accrualStartDate
        case cashFlow => cashFlow.date
    }.fold(JodaDateTimeHelper.farFuture)(JodaDateTimeHelper.min)
  }

  def maturityDate(leg: Leg): LocalDate = {
    require(leg.nonEmpty, "empty leg")

    leg.collect{
      case c: Coupon => c.accrualEndDate
      case cashFlow => cashFlow.date
    }.fold(JodaDateTimeHelper.theBeginningOfTime)(JodaDateTimeHelper.max)
  }

  def isExpired(leg: Leg, includeSettlementDateFlows: Boolean, settlementDate: LocalDate): Boolean = {
    if (leg.isEmpty) {
      true
    } else {
      leg.forall(_.hasOccurred(settlementDate, includeSettlementDateFlows))
    }
  }


  //! \name CashFlow functions

  val previousCashFlow: CashFlowFunction[Option[CashFlow]] = (leg, include, date) => leg.reverse.find(_.hasOccurred(date, include))
  val nextCashFlow: CashFlowFunction[Option[CashFlow]] = (leg, include, date) => leg.find(!_.hasOccurred(date, include))

  val previousCashFlowDate: CashFlowFunction[Option[LocalDate]]  = previousCashFlow(_,_,_).map(_.date)
  val nextCashFlowDate: CashFlowFunction[Option[LocalDate]] = nextCashFlow(_,_,_).map(_.date)

  val previousCashFlowAmount: CashFlowFunction[Double] = previousCashFlow(_,_,_).map(_.amount).sum
  val nextCashFlowAmount: CashFlowFunction[Double] = nextCashFlow(_,_,_).map(_.amount).sum


  //! \name Coupon inspectors
  private val couponRate: PartialFunction[CashFlow,Double] = { case x: Coupon => x.rate }
  private val couponNominal: PartialFunction[CashFlow,Double] = { case x: Coupon => x.nominal }

  val previousCouponRate: CashFlowFunction[Double] = previousCashFlow(_,_,_).collect(couponRate).sum
  val nextCouponRate: CashFlowFunction[Double] = nextCashFlow(_,_,_).collect(couponRate).sum

  val nominal: CashFlowFunction[Double] = nextCashFlow(_,_,_).collect(couponNominal).sum

  val accrualStartDate: CashFlowFunction[Option[LocalDate]] = nextCashFlow(_,_,_).collect{ case x: Coupon => x.accrualStartDate }
  val accrualEndDate: CashFlowFunction[Option[LocalDate]] = nextCashFlow(_,_,_).collect{ case x: Coupon => x.accrualEndDate }

  val accrualDays: CashFlowFunction[Int] = nextCashFlow(_,_,_).collect{ case x: Coupon => x.accrualDays }.sum
  val accrualPeriod: CashFlowFunction[Double] = nextCashFlow(_,_,_).collect{ case x: Coupon => x.accrualPeriod }.sum

  val accruedPeriod: CashFlowFunction[Double] = nextCashFlow(_,_,_).collect{ case x: Coupon => x.accruedPeriod(x.date) }.sum
  val accruedDays: CashFlowFunction[Double] = nextCashFlow(_,_,_).collect{ case x: Coupon => x.accruedDays(x.date) }.sum
  val accruedAmount: CashFlowFunction[Double] = nextCashFlow(_,_,_).collect{ case x: Coupon => x.accruedAmount(x.date) }.sum

 // type ValueParameter = (Leg, YieldTermStructure, Boolean, LocalDate, LocalDate)
 // type ValueFunction[T] = ValueParameter => T

  private val predicate = (cashFlow: CashFlow) => (date: LocalDate, include: Boolean) => cashFlow.hasOccurred(date, include) && cashFlow.tradingExCoupon(date)

  private val basisPoint = 1.0e-4

  def npv(leg: Leg,
          discountCurve: YieldTermStructure,
          includeSettlementDateFlows:Boolean,
          settlementDate: LocalDate,
          npvDate: LocalDate) = {

    leg.filterNot(predicate(_)(settlementDate, includeSettlementDateFlows))
      .map(cashFlow => cashFlow.amount * discountCurve.discount(cashFlow.date))
      .sum / discountCurve.discount(npvDate)
  }

  def bps(leg: Leg,
        discountCurve: YieldTermStructure,
        includeSettlementDateFlows:Boolean,
        settlementDate: LocalDate,
        npvDate: LocalDate) = {

    basisPoint * leg
      .filterNot(predicate(_)(settlementDate, includeSettlementDateFlows))
      .collect{ case cp: Coupon => cp.nominal * cp.accrualPeriod * discountCurve.discount(cp.date) }
      .sum / discountCurve.discount(npvDate)

  }

  def atmRate(leg: Leg,
      discountCurve: YieldTermStructure,
      includeSettlementDateFlows: Boolean,
      settlementDate: LocalDate,
      npvDate: LocalDate, targetNpv: Option[Double]): Double = {
    val filtered = leg
      .filterNot(predicate(_)(settlementDate, includeSettlementDateFlows))

    val npv = filtered.map(cashFlow => cashFlow.amount * discountCurve.discount(cashFlow.date)).sum
    val bps = filtered.collect{ case cp: Coupon => cp.nominal * cp.accrualPeriod * discountCurve.discount(cp.date)}.sum
    require(bps!=0.0, "null bps: impossible atm rate")

    targetNpv.map(_ * discountCurve.discount(npvDate) - npv).getOrElse(0.0) / bps

  }

//  def npv(leg: Leg,
//          y: InterestRate,
//          includeSettlementDateFlows:Boolean,
//          settlementDate: LocalDate,
//          npvDate: LocalDate) = {
//
//    leg.filterNot(predicate(_)(settlementDate, includeSettlementDateFlows))
//      .collect{
//        case cp: Coupon => (cp.accrualStartDate, cp.accrualEndDate)
//        case x =>
//      }
//      .sum / y.discountFactor(npvDate)
//  }
//
//  def bps(leg: Leg,
//          y: InterestRate,
//          includeSettlementDateFlows:Boolean,
//          settlementDate: LocalDate,
//          npvDate: LocalDate) = {
//
//    basisPoint * leg
//      .filterNot(predicate(_)(settlementDate, includeSettlementDateFlows))
//      .collect{ case cp: Coupon => cp.nominal * cp.accrualPeriod * discountCurve.discount(cp.date) }
//      .sum /  y.discountFactor.discount(npvDate)
//
//  }


//  //! At-the-money rate of the cash flows.
//  /*! The result is the fixed rate for which a fixed rate cash flow
//      vector, equivalent to the input vector, has the required NPV
//      according to the given term structure. If the required NPV is
//      not given, the input cash flow vector's NPV is used instead.
//  */
//  def atmRate(const Leg& leg,
//    const YieldTermStructure& discountCurve,
//    bool includeSettlementDateFlows,
//    Date settlementDate = Date(),
//    Date npvDate = Date(),
//    Real npv = Null<Real>()): Double
//  //@}
//
//  //! \name Yield (a.k.a. Internal Rate of Return, i.e. IRR) functions
//  /*! The IRR is the interest rate at which the NPV of the cash
//      flows equals the dirty price.
//  */
//  //@{
//  //! NPV of the cash flows.
//  /*! The NPV is the sum of the cash flows, each discounted
//      according to the given constant interest rate.  The result
//      is affected by the choice of the interest-rate compounding
//      and the relative frequency and day counter.
//  */
//  def npv(const Leg& leg,
//    const InterestRate& yield,
//  bool includeSettlementDateFlows,
//  Date settlementDate = Date(),
//  Date npvDate = Date()): Double
//  static Real npv(const Leg& leg,
//    Rate yield,
//  const DayCounter& dayCounter,
//  Compounding compounding,
//  Frequency frequency,
//  bool includeSettlementDateFlows,
//  Date settlementDate = Date(),
//  Date npvDate = Date());
//  //! Basis-point sensitivity of the cash flows.
//  /*! The result is the change in NPV due to a uniform
//      1-basis-point change in the rate paid by the cash
//      flows. The change for each coupon is discounted according
//      to the given constant interest rate.  The result is
//      affected by the choice of the interest-rate compounding
//      and the relative frequency and day counter.
//  */
//  static Real bps(const Leg& leg,
//    const InterestRate& yield,
//  bool includeSettlementDateFlows,
//  Date settlementDate = Date(),
//  Date npvDate = Date());
//  static Real bps(const Leg& leg,
//    Rate yield,
//  const DayCounter& dayCounter,
//  Compounding compounding,
//  Frequency frequency,
//  bool includeSettlementDateFlows,
//  Date settlementDate = Date(),
//  Date npvDate = Date());
//  //! Implied internal rate of return.
//  /*! The function verifies
//      the theoretical existance of an IRR and numerically
//      establishes the IRR to the desired precision.
//  */
//  static Rate yield(const Leg& leg,
//    Real npv,
//    const DayCounter& dayCounter,
//    Compounding compounding,
//    Frequency frequency,
//    bool includeSettlementDateFlows,
//    Date settlementDate = Date(),
//    Date npvDate = Date(),
//    Real accuracy = 1.0e-10,
//    Size maxIterations = 100,
//    Rate guess = 0.05);
//
//  //! Cash-flow duration.
//  /*! The simple duration of a string of cash flows is defined as
//      \f[
//      D_{\mathrm{simple}} = \frac{\sum t_i c_i B(t_i)}{\sum c_i B(t_i)}
//      \f]
//      where \f$ c_i \f$ is the amount of the \f$ i \f$-th cash
//      flow, \f$ t_i \f$ is its payment time, and \f$ B(t_i) \f$
//      is the corresponding discount according to the passed yield.
//
//      The modified duration is defined as
//      \f[
//      D_{\mathrm{modified}} = -\frac{1}{P} \frac{\partial P}{\partial y}
//      \f]
//      where \f$ P \f$ is the present value of the cash flows
//      according to the given IRR \f$ y \f$.
//
//      The Macaulay duration is defined for a compounded IRR as
//      \f[
//      D_{\mathrm{Macaulay}} = \left( 1 + \frac{y}{N} \right)
//                              D_{\mathrm{modified}}
//      \f]
//      where \f$ y \f$ is the IRR and \f$ N \f$ is the number of
//      cash flows per year.
//  */
//  static Time duration(const Leg& leg,
//    const InterestRate& yield,
//  Duration::Type type,
//  bool includeSettlementDateFlows,
//  Date settlementDate = Date(),
//  Date npvDate = Date());
//  static Time duration(const Leg& leg,
//    Rate yield,
//  const DayCounter& dayCounter,
//  Compounding compounding,
//  Frequency frequency,
//  Duration::Type type,
//  bool includeSettlementDateFlows,
//  Date settlementDate = Date(),
//  Date npvDate = Date());
//
//  //! Cash-flow convexity
//  /*! The convexity of a string of cash flows is defined as
//      \f[
//      C = \frac{1}{P} \frac{\partial^2 P}{\partial y^2}
//      \f]
//      where \f$ P \f$ is the present value of the cash flows
//      according to the given IRR \f$ y \f$.
//  */
//  static Real convexity(const Leg& leg,
//    const InterestRate& yield,
//  bool includeSettlementDateFlows,
//  Date settlementDate = Date(),
//  Date npvDate = Date());
//  static Real convexity(const Leg& leg,
//    Rate yield,
//  const DayCounter& dayCounter,
//  Compounding compounding,
//  Frequency frequency,
//  bool includeSettlementDateFlows,
//  Date settlementDate = Date(),
//  Date npvDate = Date());
//
//  //! Basis-point value
//  /*! Obtained by setting dy = 0.0001 in the 2nd-order Taylor
//      series expansion.
//  */
//  static Real basisPointValue(const Leg& leg,
//    const InterestRate& yield,
//  bool includeSettlementDateFlows,
//  Date settlementDate = Date(),
//  Date npvDate = Date());
//  static Real basisPointValue(const Leg& leg,
//    Rate yield,
//  const DayCounter& dayCounter,
//  Compounding compounding,
//  Frequency frequency,
//  bool includeSettlementDateFlows,
//  Date settlementDate = Date(),
//  Date npvDate = Date());
//
//  //! Yield value of a basis point
//  /*! The yield value of a one basis point change in price is
//      the derivative of the yield with respect to the price
//      multiplied by 0.01
//  */
//  static Real yieldValueBasisPoint(const Leg& leg,
//    const InterestRate& yield,
//  bool includeSettlementDateFlows,
//  Date settlementDate = Date(),
//  Date npvDate = Date());
//  static Real yieldValueBasisPoint(const Leg& leg,
//    Rate yield,
//  const DayCounter& dayCounter,
//  Compounding compounding,
//  Frequency frequency,
//  bool includeSettlementDateFlows,
//  Date settlementDate = Date(),
//  Date npvDate = Date());
//  //@}
//
//  //! \name Z-spread functions
//  /*! For details on z-spread refer to:
//      "Credit Spreads Explained", Lehman Brothers European Fixed
//      Income Research - March 2004, D. O'Kane
//  */
//  //@{
//  //! NPV of the cash flows.
//  /*! The NPV is the sum of the cash flows, each discounted
//      according to the z-spreaded term structure.  The result
//      is affected by the choice of the z-spread compounding
//      and the relative frequency and day counter.
//  */
//  static Real npv(const Leg& leg,
//    const boost::shared_ptr<YieldTermStructure>& discount,
//    Spread zSpread,
//    const DayCounter& dayCounter,
//    Compounding compounding,
//    Frequency frequency,
//    bool includeSettlementDateFlows,
//    Date settlementDate = Date(),
//    Date npvDate = Date());
//  //! implied Z-spread.
//  static Spread zSpread(const Leg& leg,
//    Real npv,
//    const boost::shared_ptr<YieldTermStructure>&,
//    const DayCounter& dayCounter,
//    Compounding compounding,
//    Frequency frequency,
//    bool includeSettlementDateFlows,
//    Date settlementDate = Date(),
//    Date npvDate = Date(),
//    Real accuracy = 1.0e-10,
//    Size maxIterations = 100,
//    Rate guess = 0.0);
}
