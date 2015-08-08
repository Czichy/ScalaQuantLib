package org.scalaquant.core.instruments.fixedincomes


import org.scalaquant.core.cashflows._
import org.scalaquant.core.cashflows.CashFlows.CashFlowBearing



object BondFunctions {

  implicit class CashFlowsBearingFunctionsClass(bearer: CashFlowBearing)
    extends CashFlowsFunctionsClass(bearer.cashflows) with BondIRRFunctions

   trait BondIRRFunctions {
//     static Real cleanPrice(const Bond& bond,
//       const InterestRate irr,
//       Date settlementDate = Date());
//
//     static Real dirtyPrice(const Bond& bond,
//       const InterestRate& yield,
//     Date settlementDate = Date());
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

}
