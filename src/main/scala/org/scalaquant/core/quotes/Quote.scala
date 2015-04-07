package org.scalaquant.core.quotes

trait Quote {
  def isValid: Boolean
  def value: Double
}

case class SimpleQuote(value: Double) extends Quote {
  val isValid: Boolean = true
}

case object InvalidQuote extends Quote{
  val isValid = false
  def value: Double = throw new Exception("Invalid Quote")
}

object Quote {
  def apply(value: Double): Quote = if (value == Double.NaN) InvalidQuote else SimpleQuote(value)
}