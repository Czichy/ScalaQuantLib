package org.scalaquant.core.cashflows

import org.joda.time.LocalDate

abstract class Dividend(val date: LocalDate) extends CashFlow {
  def amount(underlying: Double): Double
}

case class FixedDividend(amount: Double, override val date: LocalDate) extends Dividend(date){
  override def amount(underlying: Double): Double = amount
}

case class FractionalDividend(rate: Double, nominal: Double, override val date: LocalDate) extends Dividend(date){
  override def amount: Double =  rate * nominal
  override def amount(underlying: Double): Double = rate * underlying
}

