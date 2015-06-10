package org.scalaquant.core.quotes

case class DerivedQuote(value: Double) extends ValidQuote {

  override def map(f: Quote.Calculation): Quote = if (isValid) DerivedQuote(f(value)) else InvalidQuote
}

object DerivedQuote{
  def apply(other: Quote, f: Quote.Calculation): Quote = {
    other.flatMap(value => DerivedQuote(f(value)))
  }
}