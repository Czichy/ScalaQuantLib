package org.scalaquant.core.cashflows.coupons

import java.time.LocalDate
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
                      rate: InterestRate)
  extends Coupon{

  def amount = nominal * (rate.compoundFactor(accrualStartDate, accrualEndDate, refPeriodStart, refPeriodEnd) - 1.0)

  def accruedAmount(asOf: LocalDate): Double = {
    accruedAmount(
      rate.dc,
      asOf,
      if (tradingExCoupon(Some(asOf))) {
        -nominal * (rate.compoundFactor(asOf, accrualEndDate, refPeriodStart, refPeriodEnd) - 1.0)
      } else {
        nominal * (rate.compoundFactor(accrualStartDate, min(asOf, accrualEndDate), refPeriodStart, refPeriodEnd) - 1.0)
      }
    )
  }
}
