package org.scalaquant.core.quotes

case class CompositeQuote(quote1: Quote, quote2: Quote, f: (Double, Double) => Double) extends Quote{
  val value = f(quote1.value, quote2.value)
  val isValid = quote1.isValid && quote2.isValid
}
