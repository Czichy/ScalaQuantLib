package org.scalaquant.core.cashflows.coupons

import org.joda.time.LocalDate
import org.scalaquant.core.cashflows.coupons.pricers.Pricer
import org.scalaquant.core.common.time.BusinessDayConvention.Preceding

import org.scalaquant.core.common.time.TimeUnit
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.indexes.InterestRateIndex
import org.scalaquant.core.termstructures.YieldTermStructure
import org.scalaquant.core.types.{Rate, Spread}
import org.scalaquant.core.common.time.JodaDateTimeHelper._


abstract class FloatingRateCoupon(paymentDate: LocalDate, //the upcoming payment date of this coupon
                                  nominal: Rate,
                                  accrualStartDate: LocalDate, //usually the payment date of last coupon
                                  accrualEndDate: LocalDate, //usually the sttlement date of the coupon
                                  refPeriodStart: Option[LocalDate],
                                  refPeriodEnd: Option[LocalDate],
                                  val fixingDays: Int,
                                  val index: InterestRateIndex,
                                  val gearing: Double = 1.0,
                                  val spread: Spread = 0.0,
                                  val daycounter: DayCountConvention,
                                  val pricer: FloatingRateCoupon => Pricer,
                                  val isInArrears: Boolean = false)
  extends Coupon(paymentDate, nominal, accrualStartDate, accrualEndDate, refPeriodStart, refPeriodEnd, None) {

  require(gearing != 0.0, "empty gearing not allowed")

  def rate: Double = pricer.apply(this).swapletRate

  def accruedAmount(asOf: LocalDate): Double =
    accruedAmount(
      daycounter,
      asOf,
      nominal * rate * index.dayCounter.fractionOfYear(accrualStartDate, min(date, accrualEndDate), refPeriodStart, refPeriodEnd)
    )


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
