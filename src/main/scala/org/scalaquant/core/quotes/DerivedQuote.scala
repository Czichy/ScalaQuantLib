package org.scalaquant.core.quotes

case class DerivedQuote(quote: Quote, f: Double => Double) extends Quote{
  val value = f(quote.value)
  val isValid = quote.isValid
}
