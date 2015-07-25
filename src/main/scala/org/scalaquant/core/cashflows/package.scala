package org.scalaquant.core

import org.joda.time.LocalDate

package object cashflows {

  type Leg = List[CashFlow]

  case class SimpleCashFlow(amount: Double, date: LocalDate) extends CashFlow

  case class Redemption(amount: Double, date: LocalDate) extends CashFlow

  case class AmortizingPayment(amount: Double, date: LocalDate) extends CashFlow

}

