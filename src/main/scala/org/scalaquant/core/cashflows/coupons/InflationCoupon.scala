package org.scalaquant.core.cashflows.coupons

import org.joda.time.LocalDate
import org.scalaquant.core.common.time.BusinessDayConvention._
import org.scalaquant.core.common.time.Period
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.common.time.TimeUnit._
import org.scalaquant.core.indexes.inflation.InflationIndex
import org.scalaquant.core.termstructures.YieldTermStructure
import org.scalaquant.core.types.Rate

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._
import org.scalaquant.core.common.time.JodaDateTimeHelper._


abstract class InflationCoupon(paymentDate: LocalDate, //the upcoming payment date of this coupon
                               nominal: Rate,
                               accrualStartDate: LocalDate, //usually the payment date of last coupon
                               accrualEndDate: LocalDate, //usually the sttlement date of the coupon
                               refPeriodStart: Option[LocalDate],
                               refPeriodEnd: Option[LocalDate],
                               val fixingDays: Int,
                               val index: InflationIndex,
                               val observationLag: Period,
                               val dayCounter: DayCountConvention,
                               exCouponDate: Option[LocalDate],
                               val pricer: Pricer[InflationCoupon]
                              )
  extends Coupon(paymentDate, nominal, accrualStartDate, accrualEndDate, refPeriodStart, refPeriodEnd, exCouponDate){

  def indexFixing: Rate = index.fixing(fixingDate)

  // fixing calendar is usually the null calendar for inflation indices
  def fixingDate: LocalDate =
     index.fixingCalendar.advance(refPeriodEnd - observationLag, -fixingDays, Days, ModifiedPreceding)


  def rate = pricer.swapletRate

  def accruedAmount(asOf: LocalDate): Rate = {
    if (date <= accrualStartDate || date > paymentDate)
      0.0
    else
      nominal * rate * dayCounter.fractionOfYear(accrualStartDate, min(date, accrualEndDate))
  }

  def price(discountingCurve: YieldTermStructure): Double = amount * discountingCurve.discount(date)

}
