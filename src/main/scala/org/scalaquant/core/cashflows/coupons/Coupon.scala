package org.scalaquant.core.cashflows.coupons

import org.joda.time.LocalDate
import org.scalaquant.common.time.JodaDateTimeHelper._
import org.scalaquant.common.time.daycounts.DayCountConvention
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
                      val exCouponDate: LocalDate) extends CashFlow with Accured {

  override def date: LocalDate = paymentDate

  def rate: Rate

  def dayCounter: DayCountConvention
}

trait Accrual{
  def dayCounter: DayCountConvention
  def accrualStartDate: LocalDate //usually the payment date of last coupon
  def accrualEndDate: LocalDate //usually the settlement date of the coupon
  def refPeriodStart: Option[LocalDate]
  def refPeriodEnd: Option[LocalDate]

  def accrualDays: Int = dayCounter.dayCount(accrualStartDate, accrualEndDate)

  def accrualPeriod: YearFraction = {
    dayCounter.fractionOfYear(accrualStartDate, accrualEndDate, refPeriodStart, refPeriodEnd)
  }

}

trait Accured extends Accrual{

  def paymentDate: LocalDate

  private def isNotInAccrualPeriod(d: LocalDate) = d <= accrualStartDate || d > paymentDate

  def accruedPeriod(date: LocalDate): YearFraction = {
    if (isNotInAccrualPeriod(date)) 0.0
    else dayCounter.fractionOfYear(accrualStartDate, min(date, accrualEndDate), refPeriodStart, refPeriodEnd)
  }

  def accruedDays(date: LocalDate): Int = {
    if (isNotInAccrualPeriod(date)) 0
    else dayCounter.dayCount(accrualStartDate, min(date, accrualEndDate))
  }

  def accruedAmount(date: LocalDate): Double

}

