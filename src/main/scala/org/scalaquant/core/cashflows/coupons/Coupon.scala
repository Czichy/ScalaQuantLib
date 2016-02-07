package org.scalaquant.core.cashflows.coupons

import java.time.LocalDate
import org.scalaquant.core.common.time.JodaDateTimeHelper._
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.cashflows.CashFlow
import org.scalaquant.core.types.{YearFraction, Rate}

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

trait Coupon {

  def paymentDate: LocalDate //the upcoming payment date of this coupon

  def nominal: Rate

  def accrualStartDate: LocalDate //usually the payment date of last coupon

  def accrualEndDate: LocalDate //usually the settlement date of the coupon

  def refPeriodStart: Option[LocalDate]

  def refPeriodEnd: Option[LocalDate]

  def exCouponDate: Option[LocalDate]

  def date = paymentDate

  protected def period = (_: DayCountConvention).fractionOfYear(accrualStartDate, _: LocalDate, refPeriodStart, refPeriodEnd)

  protected def days = (_: DayCountConvention).dayCount(accrualStartDate, _: LocalDate)

  protected def accruedAmount(dayCounter: DayCountConvention, asOf: LocalDate, subFunction: => Double): Double = {
    if (asOf <= accrualStartDate || asOf > paymentDate) 0.0 else subFunction
  }

  def tradingExCoupon(refDate: Option[LocalDate])(implicit evaluationDate: LocalDate): Boolean = {
     exCouponDate exists { _ <= refDate.getOrElse(evaluationDate) }
  }

  def accrualDays(dayCounter: DayCountConvention): Int = {
    days(dayCounter, accrualEndDate)
  }

  def accrualPeriod(dayCounter: DayCountConvention): YearFraction = {
    period(dayCounter, accrualEndDate)
  }

  def accruedDays(dayCounter: DayCountConvention, asOf: LocalDate): Int = {
    if (asOf <= accrualStartDate || asOf > paymentDate) 0 else days(dayCounter, min(asOf, accrualEndDate))
  }

  def accruedPeriod(dayCounter: DayCountConvention, asOf: LocalDate): YearFraction = {
    if (asOf <= accrualStartDate || asOf > paymentDate) 0.0 else period(dayCounter, min(asOf, accrualEndDate))
  }

}
