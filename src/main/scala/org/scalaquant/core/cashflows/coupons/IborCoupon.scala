package org.scalaquant.core.cashflows.coupons

import java.time.LocalDate

import org.scalaquant.core.cashflows.coupons.pricers.Pricer
import org.scalaquant.core.common.time.TimeUnit.Days
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.indexes.ibor.IBORIndex
import org.scalaquant.core.types.{Rate, Spread}
import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

final case class IBORCoupon(paymentDate: LocalDate, //the upcoming payment date of this coupon
                      nominal: Rate,
                      accrualStartDate: LocalDate, //usually the payment date of last coupon
                      accrualEndDate: LocalDate, //usually the sttlement date of the coupon
                      refPeriodStart: Option[LocalDate],
                      refPeriodEnd: Option[LocalDate],
                      exCouponDate: Option[LocalDate],
                      fixingDays: Int,
                      index: IBORIndex,
                      gearing: Double = 1.0,
                      spread: Spread = 0.0,
                      dayCounter: DayCountConvention,
                      //pricer: IBORCoupon => Pricer,
                      isInArrears: Boolean = false)
  extends FloatingRateCoupon{

  private val fixingCalendar = index.fixingCalendar
  private val indexFixingDays = index.fixingDays
  private val fixingValueDate = fixingCalendar.advance(fixingDate, indexFixingDays, Days)
  private val fixingEndDate = index.maturityDate(fixingValueDate)
  private val spanningTime = dayCounter.fractionOfYear(fixingValueDate, fixingEndDate)

  def indexFixing(implicit evaluationDate: LocalDate): Option[Rate] = {

    if (fixingDate > evaluationDate) index.forecastFixing(fixingValueDate, fixingEndDate, spanningTime)
    else index.pastFixing(fixingDate)

  }

}

