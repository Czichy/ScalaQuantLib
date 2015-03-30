package org.scalaquant.core.quotes

trait Quote {
  def isValid: Boolean
  def value: Double
}

case object InvalidQuote extends Quote{
  val isValid = false
  def value: Double = throw new Exception("Invalid Quote")
}
