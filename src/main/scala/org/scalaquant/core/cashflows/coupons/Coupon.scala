package org.scalaquant.core.cashflows.coupons

import org.joda.time.LocalDate
import org.scalaquant.core.common.time.JodaDateTimeHelper._
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.cashflows.CashFlow
import org.scalaquant.core.types.{YearFraction, Rate}

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

abstract class Coupon(val paymentDate: LocalDate, //the upcoming payment date of this coupon
                      val nominal: Rate,
                      val accrualStartDate: LocalDate, //usually the payment date of last coupon
                      val accrualEndDate: LocalDate, //usually the settlement date of the coupon
                      val refPeriodStart: Option[LocalDate],
                      val refPeriodEnd: Option[LocalDate],
                      val exCouponDate: Option[LocalDate])
  extends CashFlow(exCouponDate, nominal) {

  protected def notInRange = date <= accrualStartDate || date > paymentDate

  protected def period = (_: DayCountConvention).fractionOfYear(accrualStartDate, _: LocalDate, refPeriodStart, refPeriodEnd)

  protected def days = (_: DayCountConvention).dayCount(accrualStartDate, _: LocalDate)

  protected def minDate: LocalDate = min(date, accrualEndDate)

  def accruedAmountAt(date: LocalDate): Double

  def tradingExCoupon(refDate: Option[LocalDate])(implicit evaluationDate: LocalDate): Boolean = {
     exCouponDate exists { _ <= refDate.getOrElse(evaluationDate) }
  }

  def accrualDays(dayCounter: DayCountConvention): Int = {
    if (notInRange) 0 else days(dayCounter, accrualEndDate)
  }

  def accrualPeriod(dayCounter: DayCountConvention): YearFraction = {
    if (notInRange) 0.0 else period(dayCounter, accrualEndDate)
  }

  def accruedDays(dayCounter: DayCountConvention): Int = {
    if (notInRange) 0 else days(dayCounter, minDate)
  }

  def accruedPeriod(dayCounter: DayCountConvention): YearFraction = {
    if (notInRange) 0.0 else period(dayCounter, minDate)
  }

}
