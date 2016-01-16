package org.scalaquant.core.cashflows

import java.time.LocalDate

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._
import org.scalaquant.math.Comparison.Order

private [cashclows] trait CashFlow{
  def date: LocalDate
  def amount: Double
}


object CashFlow{

  implicit object CashFlowOrder extends Order[CashFlow]{
    def >(x: CashFlow, y: CashFlow) : Boolean = x.date > y.date
    def <(x: CashFlow, y: CashFlow) : Boolean = x.date < y.date
  }

}