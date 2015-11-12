package org.scalaquant.core.quotes


trait Quote {
  def value: Double
  protected def isValid: Boolean

}

trait ValidQuote extends Quote {
  final protected val isValid = true
}

case object InvalidQuote extends Quote {
  final protected val isValid = false
  final val value = Double.NaN
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

  val testing: (Double, Double) => String = {
    case (amount, balance) =>
      {}
  }
}
