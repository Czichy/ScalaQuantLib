package org.scalaquant.core.currencies

import org.scalaquant.core.common.Money
import org.scalaquant.core.currencies.ExchangeRate.{ Derived, Direct, ExchangeType }

/**
 * Created by neo on 2015-03-03.
 */
case class ExchangeRate(source: Currency, target: Currency, rate: Double, exchangeType: ExchangeType = Direct) {
  def exchange(money: Money): Money = {
    exchangeType match {
      case Direct =>
        if (money.currency == source)
          Money(money.value * rate, target)
        else if (money.currency == target)
          Money(money.value / rate, source)
        else money
      case Derived(rate1, rate2) =>
        if (money.currency == rate1.source || money.currency == rate1.target)
          rate2.exchange(rate1.exchange(money))
        else if (money.currency == rate2.source || money.currency == rate2.target)
          rate1.exchange(rate2.exchange(money))
        else money
    }
  }
}

object ExchangeRate {
  trait ExchangeType
  case object Direct extends ExchangeType
  case class Derived(rate1: ExchangeRate = Unknown, rate2: ExchangeRate = Unknown) extends ExchangeType
  val Unknown = ExchangeRate(Currency.NullCurrency, Currency.NullCurrency, 0.0)
  val UnChainable = ExchangeRate(Currency.NullCurrency, Currency.NullCurrency, 0.0, Derived(Unknown, Unknown))
  def chain(rate1: ExchangeRate, rate2: ExchangeRate): ExchangeRate = {
    val exchangeType = Derived(rate1, rate2)
    if (rate1.source == rate2.source) {
      ExchangeRate(rate1.target, rate2.target, rate2.rate / rate1.rate, exchangeType)
    } else if (rate1.source == rate2.target) {
      ExchangeRate(rate1.target, rate2.source, 1.0 / (rate1.rate * rate2.rate), exchangeType)
    } else if (rate1.target == rate2.source) {
      ExchangeRate(rate1.source, rate2.target, rate1.rate * rate2.rate, exchangeType)
    } else if (rate1.target == rate2.target) {
      ExchangeRate(rate1.source, rate2.source, rate1.rate / rate2.rate, exchangeType)
    } else {
      UnChainable
    }
  }
}