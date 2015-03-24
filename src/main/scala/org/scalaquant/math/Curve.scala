package org.scalaquant.math

trait Curve extends (Double => Double)

class TestCurve extends Curve {
  def apply(x: Double): Double = Math.sin(x)
}