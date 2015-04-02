package org.scalaquant.core.cashflows

import org.joda.time.LocalDate
import org.scalaquant.core.common.Settings
import org.scalaquant.core.common.time.daycounts.DayCountConvention

import scala.language.implicitConversions
import org.scalaquant.core.common.time.JodaDateTimeHelper._

abstract class Coupon(val paymentDate: LocalDate,
                      val nominal: Double,
                      val accrualStartDate: LocalDate,
                      val accrualEndDate: LocalDate,
                      exCouponDate: LocalDate = Settings.evaluationDate) extends CashFlow {

  override def date: LocalDate = paymentDate

  def accrualDays: Int = dayCounter.dayCount(accrualStartDate, accrualEndDate)
  def accrualPeriod: Double = dayCounter.fraction(accrualStartDate, accrualEndDate, paymentDate)

  private def isNotInAccrualPeriod(date: LocalDate) = date <= accrualStartDate || date > paymentDate

  def accruedDays(date: LocalDate): Int = {
    if (isNotInAccrualPeriod(date))
      0
    else
      dayCounter.dayCount(accrualStartDate, min(date, accrualEndDate))
  }
  def accruedPeriod(date: LocalDate): Double = {
    if (isNotInAccrualPeriod(date))
      0.0
    else
      dayCounter.fraction(accrualStartDate, min(date, accrualEndDate), paymentDate)
  }

  def accruedAmount(date: LocalDate): Double
  def rate: Double
  def dayCounter: DayCountConvention

}
