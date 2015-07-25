package org.scalaquant.core.cashflows.coupons

import org.joda.time.LocalDate
import org.scalaquant.common.Compounding.Simple
import org.scalaquant.common.InterestRate
import org.scalaquant.common.time.Frequency
import org.scalaquant.common.time.JodaDateTimeHelper._
import org.scalaquant.common.time.daycounts.DayCountConvention

class FixedRateCoupon(val paymentDate: LocalDate,
                      val nominal: Double,
                      val rate: InterestRate,
                      val accrualStartDate: LocalDate,
                      val accrualEndDate: LocalDate,
                      override val exCouponDate: LocalDate)
  extends Coupon(paymentDate, nominal, accrualStartDate, accrualEndDate, exCouponDate) {

  def dayCounter = rate.dc

  def this(paymentDate: LocalDate,
           nominal: Double,
           rate: Double,
           dayCounter: DayCountConvention,
           accrualStartDate: LocalDate,
           accrualEndDate: LocalDate,
           exCouponDate: LocalDate) = {
    this(paymentDate, nominal, InterestRate(rate, dayCounter, Simple, Frequency.Annual), accrualStartDate, accrualEndDate, exCouponDate)
  }


  def amount: Double = nominal * rate.compoundFactor(accrualStartDate, accrualEndDate, paymentDate)

  override def accruedAmount(date: LocalDate): Double = {
    if (date <= accrualStartDate || date > paymentDate) {
      0.0
    } else if (tradingExCoupon(date)) {
       -nominal * (rate.compoundFactor(date, accrualEndDate, accrualStartDate) - 1.0)
    } else {
      nominal * (rate.compoundFactor(accrualStartDate, accrualEndDate, date) - 1.0)
    }
  }
}
