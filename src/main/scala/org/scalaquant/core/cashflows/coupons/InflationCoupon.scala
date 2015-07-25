package org.scalaquant.core.cashflows.coupons

import org.joda.time.LocalDate
import org.scalaquant.common.time.BusinessDayConvention._
import org.scalaquant.common.time.Period
import org.scalaquant.common.time.daycounts.DayCountConvention
import org.scalaquant.common.time.TimeUnit._
import org.scalaquant.core.indexes.inflation.InflationIndex
import org.scalaquant.core.termstructures.YieldTermStructure
import org.scalaquant.core.types.Rate

abstract class InflationCoupon(paymentDate: LocalDate,
                               nominal: Rate,
                               startDate: LocalDate,
                               endDate: LocalDate,
                               val fixingDays: Int,
                               val index: InflationIndex,
                               val observationLag: Period,
                               val dayCounter: DayCountConvention,
                               refPeriodStart: LocalDate,
                               refPeriodEnd: LocalDate,
                               pricer: InflationCouponPricer, //TODO: rethink how price should be set
                               exCouponDate: LocalDate)
  extends Coupon(paymentDate, nominal, startDate, endDate, exCouponDate){


  def indexFixing: Rate = index.fixing(fixingDate)

  // fixing calendar is usually the null calendar for inflation indices
  def fixingDate: LocalDate =
     index.fixingCalendar.advance(refPeriodEnd - observationLag, -fixingDays, Days, ModifiedPreceding)


  override def rate = pricer.swapletRate

  override def accruedAmount(date: LocalDate): Rate = {
    if (date <= accrualStartDate || date > paymentDate)
      0.0
    else
      nominal * rate * dayCounter.fractionOfYear(startDate, min(date, endDate))
  }

  def price(discountingCurve: YieldTermStructure): Double = amount * discountingCurve.discount(date)

}
