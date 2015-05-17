package org.scalaquant.core.cashflows.coupons

import org.joda.time.LocalDate
import org.scalaquant.core.cashflows.CashFlow
import org.scalaquant.core.common.Settings
import org.scalaquant.core.common.time.JodaDateTimeHelper._
import org.scalaquant.core.common.time.daycounts.DayCountConvention

import scala.language.implicitConversions

abstract class Coupon(val paymentDate: LocalDate,
                      val nominal: Double,
                      val accrualStartDate: LocalDate,
                      val accrualEndDate: LocalDate,
                      override val exCouponDate: LocalDate = Settings.evaluationDate) extends CashFlow {

  override def date: LocalDate = paymentDate

  def accrualDays: Int = dayCounter.dayCount(accrualStartDate, accrualEndDate)
  def accrualPeriod: Double = dayCounter.fractionOfYear(accrualStartDate, accrualEndDate, paymentDate)

  private def isNotInAccrualPeriod(date: LocalDate) = date <= accrualStartDate || date > paymentDate

  def accruedDays(date: LocalDate): Int = {
    if (isNotInAccrualPeriod(date)) 0
    else dayCounter.dayCount(accrualStartDate, min(date, accrualEndDate))
  }
  def accruedPeriod(date: LocalDate): Double = {
    if (isNotInAccrualPeriod(date))
      0.0
    else
      dayCounter.fractionOfYear(accrualStartDate, min(date, accrualEndDate), paymentDate)
  }

  def accruedAmount(date: LocalDate): Double
  def rate: Double
  def dayCounter: DayCountConvention

}

