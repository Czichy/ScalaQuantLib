package org.scalaquant.core.instruments.fixedincomes

import org.scalaquant.core.cashflows.coupons.Coupon
import org.scalaquant.core.cashflows.{CashFlows, CashFlow, Leg}
import org.scalaquant.core.common.Compounding
import org.scalaquant.core.common.time.Frequency
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.instruments.Instrument
import org.joda.time.LocalDate
import org.scalaquant.core.pricingengines.PricingEngine


class Bond(val settlementDays: Int, val calendar: BusinessCalendar, val issueDate: LocalDate, val coupons: Leg = Nil ) extends Instrument{

  def notionals: List[Double]
  def notional(date: LocalDate)

  def cashflows: Leg = coupons.sorted
  def redemptions: Leg

  def redemption: CashFlow

  def startDate: LocalDate
  def maturityDate: LocalDate

  def isTradable(date: LocalDate): Boolean
  def settlementDate(date: LocalDate): LocalDate

  def isExpired: Boolean = CashFlows.isExpired(coupons, includeSettlementDateFlows = true, Settings.evaluationDate)
  def cleanPrice: Double

  //! theoretical dirty price
  /*! The default bond settlement is used for calculation.

      \warning the theoretical price calculated from a flat term
               structure might differ slightly from the price
               calculated from the corresponding yield by means
               of the other overload of this function. If the
               price from a constant yield is desired, it is
               advisable to use such other overload.
  */
  def dirtyPrice: Double

  //! theoretical settlement value
  /*! The default bond settlement date is used for calculation. */
  def settlementValue: Double

  //! theoretical bond yield
  /*! The default bond settlement and theoretical price are used
      for calculation.
  */
  def yieldOf(dc: DayCountConvention,
     comp: Compounding,
     freq: Frequency,
     accuracy: Double = 1.0e-8,
     maxEvaluations: Int = 100): Double = ???

  //! clean price given a yield and settlement date
  /*! The default bond settlement is used if no date is given. */
  def cleanPrice(yieldOf: Double,
                 dc: DayCountConvention,
                 comp: Compounding,
                 freq: Frequency,
                 settlementDate: LocalDate): Double = ???

  //! dirty price given a yield and settlement date
  /*! The default bond settlement is used if no date is given. */
  def dirtyPrice(yieldOf: Double,
    dc: DayCountConvention,
    comp: Compounding,
    freq: Frequency,
    settlementDate: LocalDate): Double = ???

  //! settlement value as a function of the clean price
  /*! The default bond settlement date is used for calculation. */
  def settlementValue(cleanPrice: Double): Double

  //! yield given a (clean) price and settlement date
  /*! The default bond settlement is used if no date is given. */
  def yieldOf(cleanPrice: Double,
              dc: DayCountConvention,
              comp: Compounding,
              freq: Frequency,
              settlementDate: LocalDate,
              accuracy: Double = 1.0e-8,
              maxEvaluations: Int = 100): Double = ???

  //! accrued amount at a given date
  /*! The default bond settlement is used if no date is given. */
  def accruedAmount(date: LocalDate): Double
  //@}

  /*! Expected next coupon: depending on (the bond and) the given date
      the coupon can be historic, deterministic or expected in a
      stochastic sense. When the bond settlement date is used the coupon
      is the already-fixed not-yet-paid one.

      The current bond settlement is used if no date is given.
  */
  def nextCouponRate(date: LocalDate): Double

  //! Previous coupon already paid at a given date
  /*! Expected previous coupon: depending on (the bond and) the given
      date the coupon can be historic, deterministic or expected in a
      stochastic sense. When the bond settlement date is used the coupon
      is the last paid one.

      The current bond settlement is used if no date is given.
  */
  def previousCouponRate(date: LocalDate): Double

  def nextCashFlowDate(date: LocalDate): LocalDate

  def previousCashFlowDate(date: LocalDate): LocalDate

  protected def addRedemptionsToCashflows(redemptions: List[Double]): Bond = ???

  /*! This method can be called by derived classes in order to
      build a bond with a single redemption payment.  It will
      fill the notionalSchedule_, notionals_, and redemptions_
      data members.
  */
  protected def setSingleRedemption(notional: Double, redemption: Double, date: LocalDate): Bond = ???

  /*! This method can be called by derived classes in order to
      build a bond with a single redemption payment.  It will
      fill the notionalSchedule_, notionals_, and redemptions_
      data members.
  */
  protected def setSingleRedemption(notional: Double, redemption: CashFlow): Bond = ???

  /*! used internally to collect notional information from the
      coupons. It should not be called by derived classes,
      unless they already provide redemption cash flows (in
      which case they must set up the redemptions_ data member
      independently).  It will fill the notionalSchedule_ and
      notionals_ data members.
  */
  private final def  calculateNotionalsFromCashflows: List[Double] = {
//    notionalSchedule_.clear();
//    notionals_.clear();
//
//    Date lastPaymentDate = Date();
//    notionalSchedule_.push_back(Date());
    cashflows.collect{
        case coupon: Coupon =>
          val notional = coupon.nominal
          val

      }
    }
    for (Size i=0; i<cashflows_.size(); ++i) {
      shared_ptr<Coupon> coupon =
        boost::dynamic_pointer_cast<Coupon>(cashflows_[i]);
      if (!coupon)
        continue;

      Real notional = coupon->nominal();
      // we add the notional only if it is the first one...
      if (notionals_.empty()) {
        notionals_.push_back(coupon->nominal());
        lastPaymentDate = coupon->date();
      } else if (!close(notional, notionals_.back())) {
        // ...or if it has changed.
        QL_REQUIRE(notional < notionals_.back(),
          "increasing coupon notionals");
        notionals_.push_back(coupon->nominal());
        // in this case, we also add the last valid date for
        // the previous one...
        notionalSchedule_.push_back(lastPaymentDate);
        // ...and store the candidate for this one.
        lastPaymentDate = coupon->date();
      } else {
        // otherwise, we just extend the valid range of dates
        // for the current notional.
        lastPaymentDate = coupon->date();
      }
    }
    QL_REQUIRE(!notionals_.empty(), "no coupons provided");
    notionals_.push_back(0.0);
    notionalSchedule_.push_back(lastPaymentDate);
  }
}


object Bond {
  case class Arguments(settlementDate: LocalDate, cashflows: Leg, calendar: BusinessCalendar) extends PricingEngine.Arguments
  case class Results(settlementValue: Double) extends PricingEngine.Results

}