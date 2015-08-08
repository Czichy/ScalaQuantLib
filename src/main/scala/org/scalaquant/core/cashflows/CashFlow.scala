package org.scalaquant.core.cashflows

import org.joda.time.LocalDate

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

import org.scalaquant.common.Event

abstract class CashFlow extends Event {

  def amount: Double

  def exCouponDate: LocalDate

  def tradingExCoupon(refDate: LocalDate): Boolean = exCouponDate <= refDate

}

object CashFlow{

  def unapply(cashFlow: CashFlow): Option[(Double, LocalDate)] = {
    Some((cashFlow.amount, cashFlow.date))
  }
}