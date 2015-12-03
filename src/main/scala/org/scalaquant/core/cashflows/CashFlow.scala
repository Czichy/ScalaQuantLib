package org.scalaquant.core.cashflows

import org.joda.time.LocalDate
import org.scalaquant.core.common.Event

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._
import org.scalaquant.math.Comparison.Order

class CashFlow(val date: LocalDate, val amount: Double)

object CashFlow{

  implicit object CashFlowRelational extends Order[CashFlow]{
    def >(x: CashFlow, y: CashFlow) : Boolean = x.date > y.date
    def <(x: CashFlow, y: CashFlow) : Boolean = x.date < y.date
  }

}