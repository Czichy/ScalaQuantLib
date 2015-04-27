package org.scalaquant.core.quotes


trait Quote {
  def value: Double
  protected def isValid: Boolean

  def flatMap(f: Double => ValidQuote): Quote = {
    if (isValid) f(value) else InvalidQuote
  }

  def map(f: Double => Double): Quote
}

trait ValidQuote extends Quote {
  final protected val isValid = true
}

case object InvalidQuote extends Quote {
  final protected val isValid = false
  final val value = Double.NaN
  def map(f: Double => Double): Quote = this
}


case class SimpleQuote(value: Double) extends ValidQuote {
  def map(f: Double => Double): Quote = {
    if (isValid) SimpleQuote(f(value)) else InvalidQuote
  }
}

object Quote {
  def apply(value: Double): Quote = if (value == Double.NaN) InvalidQuote else SimpleQuote(value)
}
