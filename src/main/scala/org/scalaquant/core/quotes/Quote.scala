package org.scalaquant.core.quotes


trait Quote {
  import Quote._

  def value: Double
  protected def isValid: Boolean

  def flatMap(f: Double => ValidQuote): Quote = {
    if (isValid) f(value) else InvalidQuote
  }

  def map(f: Calculation): Quote
}

trait ValidQuote extends Quote {
  final protected val isValid = true
}

case object InvalidQuote extends Quote {
  import Quote._
  final protected val isValid = false
  final val value = Double.NaN
  def map(f: Calculation): Quote = this
}


case class SimpleQuote(value: Double) extends ValidQuote {
  import Quote._
  def map(f: Calculation): Quote = {
    if (isValid) SimpleQuote(f(value)) else InvalidQuote
  }
}

object Quote {
  type Calculation = Double => Double

  def apply(value: Double): Quote = if (value == Double.NaN) InvalidQuote else SimpleQuote(value)
}
