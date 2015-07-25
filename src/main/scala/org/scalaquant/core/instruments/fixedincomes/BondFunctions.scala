package org.scalaquant.core.instruments.fixedincomes

import org.joda.time.LocalDate
import org.scalaquant.core.cashflows.CashFlows._

import org.scalaquant.core.cashflows._
import org.scalaquant.core.termstructures._
import org.scalaquant.core.types._


object BondFunctions {


    implicit class BondFunction[T <: Bond](val bond: T) extends CashFlowFunctions[T] with CouponFunctions[T] {
      // Date inspectors
      protected def leg: Leg = bond.cashflows
      def startDate: LocalDate = CashFlows.startDate(leg)
      def maturityDate: LocalDate = CashFlows.maturityDate(leg)
      def isTradeableAt(settlementDate: LocalDate): Boolean = bond.notionalAt(settlementDate) != 0.0
    }

   trait CashFlowFunctions[T] { self: BondFunction[T] =>

     type CashFlowResult[R] = CashFlowFunction[R] => R
     protected def on[R](date: LocalDate): CashFlowResult[R] = _.apply(leg, false, date)

     def previousCashFlow(refDate: LocalDate): Option[CashFlow] = on(refDate)(CashFlows.previousCashFlow)
     def nextCashFlow(refDate: LocalDate): Option[CashFlow] = on(refDate)(CashFlows.nextCashFlow)

     def previousCashFlowDate(refDate: LocalDate): Option[LocalDate] = on(refDate)(CashFlows.previousCashFlowDate)
     def nextCashFlowDate(refDate: LocalDate): Option[LocalDate] = on(refDate)(CashFlows.previousCashFlowDate)

     def previousCashFlowAmount(refDate: LocalDate): Double = on(refDate)(CashFlows.previousCashFlowAmount)
     def nextCashFlowAmount(refDate: LocalDate): Double = on(refDate)(CashFlows.nextCashFlowAmount)
   }

   trait CouponFunctions[T] extends CashFlowFunctions[T] { self: BondFunction[T] =>

     def previousCouponRate(settlementDate: LocalDate): Rate = on(settlementDate)(CashFlows.previousCouponRate)
     def nextCouponRate(settlementDate: LocalDate): Rate = on(settlementDate)(CashFlows.previousCouponRate)

     def accrualStartDate(settlementDate: LocalDate): Option[LocalDate] = on(settlementDate)(CashFlows.accrualStartDate)
     def accrualEndDate(settlementDate: LocalDate): Option[LocalDate] = on(settlementDate)(CashFlows.accrualEndDate)

//     def referencePeriodStart(settlementDate: LocalDate): LocalDate = CashFlows
//     def referencePeriodEnd(settlementDate: LocalDate): LocalDate

     def accrualPeriod(settlementDate: LocalDate): YearFraction = on(settlementDate)(CashFlows.accrualPeriod)
     def accruedPeriod(settlementDate: LocalDate): YearFraction = on(settlementDate)(CashFlows.accruedPeriod)

     def accrualDays(settlementDate: LocalDate): Long = on(settlementDate)(CashFlows.accrualDays)
     def accruedDays(settlementDate: LocalDate): Long = on(settlementDate)(CashFlows.accruedDays)

     def accruedAmount(settlementDate: LocalDate): Double = on(settlementDate)(CashFlows.accruedAmount)
   }

   trait YieldTermStructureFunctions[T] {

     def cleanPrice(discountCurve: YieldTermStructure, settlementDate: LocalDate ): Double
     def bps(discountCurve: YieldTermStructure, settlementDate: LocalDate): Double
     def atmRate(discountCurve: YieldTermStructure, settlementDate: LocalDate, clearPrice: Option[Double]): Double
   }


   trait IRRFunctions[T] { self: T =>
//     static Real cleanPrice(const Bond& bond,
//       const InterestRate irr,
//       Date settlementDate = Date());
//
//     static Real dirtyPrice(const Bond& bond,
//       const InterestRate& yield,
//     Date settlementDate = Date());
//
//     static Real bps(const Bond& bond,
//       const InterestRate& yield,
//     Date settlementDate = Date());
//
//     static Rate yield(const Bond& bond,
//       Real cleanPrice,
//       const DayCounter& dayCounter,
//       Compounding compounding,
//       Frequency frequency,
//       Date settlementDate = Date(),
//       Real accuracy = 1.0e-10,
//       Size maxIterations = 100,
//       Rate guess = 0.05);
//
//     static Time duration(const Bond& bond,
//       const InterestRate& yield,
//     Duration::Type type = Duration::Modified,
//     Date settlementDate = Date() );
//
//     static Real convexity(const Bond& bond,
//       const InterestRate& yield,
//     Date settlementDate = Date());
//
//     static Real basisPointValue(const Bond& bond,
//       const InterestRate& yield,
//     Date settlementDate = Date());
//
//     static Real yieldValueBasisPoint(const Bond& bond,
//       const InterestRate& yield,
//     Date settlementDate = Date());
//
//
//     //! \name Z-spread functions
//     //@{
//     static Real cleanPrice(const Bond& bond,
//       const boost::shared_ptr<YieldTermStructure>& discount,
//       Spread zSpread,
//       const DayCounter& dayCounter,
//       Compounding compounding,
//       Frequency frequency,
//       Date settlementDate = Date());
//
//     static Spread zSpread(const Bond& bond,
//       Real cleanPrice,
//       const boost::shared_ptr<YieldTermStructure>&,
//       const DayCounter& dayCounter,
//       Compounding compounding,
//       Frequency frequency,
//       Date settlementDate = Date(),
//       Real accuracy = 1.0e-10,
//       Size maxIterations = 100,
//       Rate guess = 0.0);
//     //@}

   }

  //trait ZSpreadFunstions{}


}
