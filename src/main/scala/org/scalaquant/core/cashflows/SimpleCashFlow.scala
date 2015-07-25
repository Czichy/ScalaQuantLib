package org.scalaquant.core.cashflows

import org.joda.time.LocalDate

abstract class SimpleCashFlow extends CashFlow
case class Redemption(amount: Double, date: LocalDate) extends SimpleCashFlow
case class AmortizingPayment(amount: Double, date:LocalDate) extends SimpleCashFlow