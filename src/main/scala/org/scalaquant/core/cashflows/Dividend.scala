package org.scalaquant.core.cashflows

import org.joda.time.LocalDate

//abstract class Dividend extends CashFlow {
//  def amount(underlying: Double): Double
//}

sealed trait Dividend extends CashFlow

case class FixedDividend(amount: Double, date: LocalDate) extends Dividend

case class FractionalDividend(rate: Double, nominal: Double, underlying: Double, date: LocalDate) extends Dividend

