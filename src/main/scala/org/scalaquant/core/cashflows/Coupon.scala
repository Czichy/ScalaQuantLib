package org.scalaquant.core.cashflows

import org.joda.time.LocalDate
import org.scalaquant.core.common.time.daycounts.DayCountConvention

import scala.language.implicitConversions
import org.scalaquant.core.common.time.JodaDateTimeHelper._

abstract class Coupon(paymentDate: LocalDate,
    nominal: Double,
    accrualStartDate: LocalDate,
    accrualEndDate: LocalDate,
    exCouponDate: LocalDate) extends CashFlow {

  protected var _dayCounter: DayCountConvention
  override def date: LocalDate = paymentDate

  def accrualDays: Int = _dayCounter.dayCount(accrualStartDate, accrualEndDate)
  def accrualPeriod: Double = _dayCounter.fraction(accrualStartDate, accrualEndDate)

  private def isNotInAccrualPeriod(date: LocalDate) = date <= accrualStartDate || date > paymentDate

  def accruedDays(date: LocalDate): Int = {
    if (isNotInAccrualPeriod(date))
      0
    else
      _dayCounter.dayCount(accrualStartDate, min(date, accrualEndDate))
  }
  def accruedPeriod(date: LocalDate): Double = {
    if (isNotInAccrualPeriod(date))
      0.0
    else
      _dayCounter.fraction(accrualStartDate, min(date, accrualEndDate))
  }

  def accruedAmount(date: LocalDate): Double
  def rate: Double
  def dayCountConvention_(dayCounter: DayCountConvention): Unit = {
    _dayCounter = dayCounter
  }

}
