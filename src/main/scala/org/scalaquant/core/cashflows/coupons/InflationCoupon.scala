package org.scalaquant.core.cashflows.coupons

import java.time.LocalDate
import org.scalaquant.core.cashflows.coupons.pricers.Pricer
import org.scalaquant.core.common.time.BusinessDayConvention._
import org.scalaquant.core.common.time.{TimeUnit, Period}
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.indexes.inflation.InflationIndex
import org.scalaquant.core.termstructures.YieldTermStructure
import org.scalaquant.core.types.Rate

import org.scalaquant.core.common.time.JodaDateTimeHelper._
import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._
import org.scalaquant.core.types.Natural

trait InflationCoupon extends Coupon {

  def fixingDays: Natural
  def index: InflationIndex
  def observationLag: Period
  def dayCounter: DayCountConvention
 // val pricer: InflationCoupon => Pricer)

  def indexFixing: Option[Rate] = fixingDate.map( index.fixing(_) )

  def fixingDate: Option[LocalDate] =
    refPeriodEnd.map{date =>
      index.fixingCalendar.advance(date - observationLag, - fixingDays, TimeUnit.Days, ModifiedPreceding)
    }


//  def rate = pricer.apply(this).swapletRate
//
//  def accruedAmount(asOf: LocalDate): Rate = {
//    if (date <= accrualStartDate || date > paymentDate)
//      0.0
//    else
//      nominal * rate * dayCounter.fractionOfYear(accrualStartDate, min(date, accrualEndDate))
//  }

  def price(discountingCurve: YieldTermStructure): Double = amount * discountingCurve.discount(date)

}
