package org.scalaquant.core.quotes

case class DerivedQuote(value: Double) extends ValidQuote {
  override def map(f: (Double) => Double): Quote = if (isValid) DerivedQuote(f(value)) else InvalidQuote
}

object DerivedQuote{
  def apply(other: Quote, f: Double => Double): Quote = {
    other.flatMap(value => DerivedQuote(f(value)))
  }
}