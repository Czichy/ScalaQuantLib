package org.scalaquant.core.cashflows.coupons

import org.joda.time.LocalDate
import org.scalaquant.core.cashflows.CashFlow
import org.scalaquant.core.common.Compounding.Simple
import org.scalaquant.core.common.InterestRate
import org.scalaquant.core.common.time.Frequency
import org.scalaquant.core.common.time.JodaDateTimeHelper._
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.types._

class FixedRateCoupon(paymentDate: LocalDate, //the upcoming payment date of this coupon
                      nominal: Rate,
                      accrualStartDate: LocalDate, //usually the payment date of last coupon
                      accrualEndDate: LocalDate, //usually the sttlement date of the coupon
                      refPeriodStart: Option[LocalDate],
                      refPeriodEnd: Option[LocalDate],
                      exCouponDate: Option[LocalDate],
                      val rate: InterestRate)
  extends Coupon(paymentDate, nominal, accrualStartDate, accrualEndDate, refPeriodStart, refPeriodEnd, exCouponDate) {

 // def dayCounter = rate.dc

  //def amount: Double = nominal * rate.compoundFactor(accrualStartDate, accrualEndDate, paymentDate)

  def accruedAmountAt(date: LocalDate)(implicit evaluationDate: LocalDate): Double = {
    if (notInRange) {
      0.0
    } else if (tradingExCoupon(Some(date))) {
       -nominal * rate.compoundFactor(date, accrualEndDate, accrualStartDate) - 1.0)
    } else {
      nominal * rate.compoundFactor(accrualStartDate, accrualEndDate, date) - 1.0)
    }
  }
}
