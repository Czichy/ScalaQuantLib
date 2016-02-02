package org.scalaquant.core.cashflows.coupons

import java.time.LocalDate
import org.scalaquant.core.cashflows.coupons.pricers.Pricer
import org.scalaquant.core.common.time.BusinessDayConvention.Preceding

import org.scalaquant.core.common.time.TimeUnit
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.indexes.InterestRateIndex
import org.scalaquant.core.termstructures.YieldTermStructure
import org.scalaquant.core.types.{Rate, Spread}
import org.scalaquant.core.common.time.JodaDateTimeHelper._


trait FloatingRateCoupon extends Coupon{

  require(gearing != 0.0, "empty gearing not allowed")

  def fixingDays: Int
  def index: InterestRateIndex
  def gearing: Double
  def spread: Spread
  def dayCounter: DayCountConvention
//    def pricer: FloatingRateCoupon => Pricer,
  def isInArrears: Boolean
//
  def fixingDate: LocalDate = {
    val refDate = if (isInArrears) accrualEndDate else accrualStartDate
    index.fixingCalendar.advance(refDate, -fixingDays, TimeUnit.Days, Preceding)
  }

  def price(discountingCurve: YieldTermStructure): Double = amount * discountingCurve.discount(date)

  def indexFixing: Double = index.fixing(fixingDate)

  def amount = rate * accrualPeriod * nominal

  def rate: Double = pricer.apply(this).swapletRate

//  def accruedAmount(asOf: LocalDate): Double =
//    accruedAmount(
//      daycounter,
//      asOf,
//      nominal * rate * index.dayCounter.fractionOfYear(accrualStartDate, min(date, accrualEndDate), refPeriodStart, refPeriodEnd)
//    )



//  def adjustedFixing: Double = rate - spread / gearing

//  protected def convexityAdjustmentImpl(fixing: Rate): Rate = if (gearing == 0.0) 0.0 else adjustedFixing - fixing

//  def convexityAdjustment: Double = convexityAdjustmentImpl(indexFixing)

}
