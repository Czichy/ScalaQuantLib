package org.scalaquant.core.cashflows

import java.time.LocalDate
import org.scalaquant.core.common.time.JodaDateTimeHelper

class SimpleCashFlow(val amount: Double, val date: LocalDate) extends CashFlow {
  override def exCouponDate: LocalDate = JodaDateTimeHelper.theBeginningOfTime
}

case class Redemption(override val amount: Double, override val date: LocalDate)
  extends SimpleCashFlow(amount, date)

case class AmortizingPayment(override val amount: Double, override val date: LocalDate)
  extends SimpleCashFlow(amount, date)