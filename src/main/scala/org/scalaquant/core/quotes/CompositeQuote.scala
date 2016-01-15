package org.scalaquant.core.quotes

case class CompositeQuote(value1: Double, value2: Double, value: Double) extends ValidQuote {

  def map(f: Double => Double): Quote = if (isValid) {
    CompositeQuote(value1, value2, f(value))
  } else {
    InvalidQuote
  }
}

object CompositeQuote{

  def apply(quote1: Quote, quote2: Quote, f: (Double, Double) => Double): Quote = {
    for{
      d1 <- quote1
      d2 <- quote2
    } yield {
      CompositeQuote(d1, d2, f(d1,d2))
    }
  }
}