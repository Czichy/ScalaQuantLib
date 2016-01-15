package org.scalaquant.core.cashflows.coupons.iborcoupons

import org.joda.time.LocalDate
import org.scalaquant.core.cashflows.coupons.FloatingRateCoupon
import org.scalaquant.core.cashflows.coupons.pricers.Pricer
import org.scalaquant.core.common.time.TimeUnit.Days
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.indexes.ibor.IBORIndex
import org.scalaquant.core.types.{Spread, Rate}

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

case class IBORCoupon(override val paymentDate: LocalDate, //the upcoming payment date of this coupon
                      override val nominal: Rate,
                      override val accrualStartDate: LocalDate, //usually the payment date of last coupon
                      override val accrualEndDate: LocalDate, //usually the sttlement date of the coupon
                      override val refPeriodStart: Option[LocalDate],
                      override val refPeriodEnd: Option[LocalDate],
                      override val fixingDays: Int,
                      override val index: IBORIndex,
                      override val gearing: Double = 1.0,
                      override val spread: Spread = 0.0,
                      override val daycounter: DayCountConvention,
                      override val pricer: IBORCoupon => Pricer,
                      override val isInArrears: Boolean = false)
  extends FloatingRateCoupon( paymentDate,
                              nominal,
                              accrualStartDate,
                              accrualEndDate,
                              refPeriodStart,
                              refPeriodEnd,
                              fixingDays,
                              index,
                              gearing,
                              spread,
                              daycounter,
                              pricer,
                              isInArrears) {
  private val fixingCalendar = index.fixingCalendar
  private val indexFixingDays = index.fixingDays
  private val fixingValueDate = fixingCalendar.advance(fixingDate, indexFixingDays, Days)
  private val fixingEndDate = index.maturityDate(fixingValueDate)
  private val spanningTime = daycounter.fractionOfYear(fixingValueDate, fixingEndDate)

  def indexFixing(implicit evaluatoinDate: LocalDate): Rate = {

    if (fixingDate > evaluatoinDate) index.forecastFixing(fixingValueDate, fixingEndDate, spanningTime)
    else index.pastFixing(fixingDate)

  }

}

