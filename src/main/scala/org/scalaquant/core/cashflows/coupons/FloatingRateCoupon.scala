package org.scalaquant.core.cashflows.coupons

import org.joda.time.LocalDate
import org.scalaquant.common.time.BusinessDayConvention.Preceding
import org.scalaquant.common.time.Frequency.Frequency
import org.scalaquant.common.time.TimeUnit
import org.scalaquant.core.cashflows.coupons.pricers.FloatingRateCouponPricer
import org.scalaquant.core.indexes.InterestRateIndex
import org.scalaquant.core.termstructures.YieldTermStructure
import org.scalaquant.core.types.{Rate, Spread}
import org.scalaquant.common.time.JodaDateTimeHelper._


abstract class FloatingRateCoupon(paymentDate: LocalDate,
                                  nominal: Double,
                                  startDate: LocalDate,
                                  endDate: LocalDate,
                                  val fixingDays: Int,
                                  val index: InterestRateIndex,
                                  val gearing: Double = 1.0,
                                  val spread: Spread = 0.0,
                                  val freq: Frequency,
                                  //refPeriodStart : LocalDate
                                  //refPeriodEnd: LocalDate
                                  pricer: FloatingRateCouponPricer, //TODO: rethink how price should be set
                                  val isInArrears: Boolean = false)
  extends Coupon(paymentDate, nominal, startDate, endDate) {

  require(gearing != 0.0, "empty gearing not allowed")

  override def rate: Double = pricer.swapletRate

  override def accruedAmount(date: LocalDate): Double = {
    if (date <= accrualStartDate || date > paymentDate) {
      0.0
    } else {
       nominal * rate * index.dayCounter.fractionOfYear(accrualStartDate, min(date, accrualEndDate), refPeriodStart_, refPeriodEnd_);
    }
  }

  def fixingDate: LocalDate = {
    val refDate = if (isInArrears) accrualEndDate else accrualStartDate
    index.fixingCalendar.advance(refDate, -fixingDays, TimeUnit.Days, Preceding)
  }

  def price(discountingCurve: YieldTermStructure): Double = amount * discountingCurve.discount(date)

  def indexFixing: Double = index.fixing(fixingDate)

  def adjustedFixing: Double = rate - spread / gearing

  protected def convexityAdjustmentImpl(fixing: Rate): Rate = if (gearing == 0.0) 0.0 else adjustedFixing - fixing

  def convexityAdjustment: Double = convexityAdjustmentImpl(indexFixing)
}

//object FloatingRateCoupon{
//  def apply(pricer: FloatingRateCouponPricer)
//}