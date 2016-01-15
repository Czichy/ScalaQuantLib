package org.scalaquant.core.quotes


trait Quote {

  def value: Double
  def isValid: Boolean

  def map(f: Double => Double): Quote

  def flatMap(f: Double => Quote): Quote = if (isValid) f(value) else InvalidQuote
}

trait ValidQuote extends Quote {
  final protected val isValid = true
}

case object InvalidQuote extends Quote {
  def map(f: Double => Double): Quote = this
  final protected val isValid = false
  final val value = Double.NaN
}


case class SimpleQuote(value: Double) extends ValidQuote {

  def map(f: Double => Double): Quote = {
    if (isValid) SimpleQuote(f(value)) else InvalidQuote
  }
}


object Quote {

  def apply(value: Double): Quote = if (value == Double.NaN) InvalidQuote else SimpleQuote(value)

}
