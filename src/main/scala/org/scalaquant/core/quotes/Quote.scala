package org.scalaquant.core.quotes

trait Quote {
  def value: Double
  def isValid: Boolean
}

trait Quote extends