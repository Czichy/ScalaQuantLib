package org.scalaquant.core.quotes

case class SimpleQuote(value: Double = Double.NaN) extends Quote {
  override def isValid: Boolean = value != Double.NaN

//  def updateValue(newValue: Double): Double = {
//    val diff = newValue - _value
//    if (diff != 0.0) subscribe()
//
//    diff
//  }
//
//  //def reset(): Unit = _value = Double.NaN
}
