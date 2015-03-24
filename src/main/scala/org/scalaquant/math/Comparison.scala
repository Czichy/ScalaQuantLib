package org.scalaquant.math

import Constants._

object Comparison {
  implicit class DoubleOps(val value: Double) extends AnyVal{
    def ~=(other: Double): Boolean = ~=(other, 42)
    def ~=(other: Double, size: Int):Boolean = {
      if (value == other) {
        true
      } else {
        val diff = Math.abs( value - other )
        val tolerance = size * QL_EPSILON
        if (value * other == 0.0) // x or y = 0.0
          diff < (tolerance * tolerance)
        else
          diff <= tolerance * Math.abs(value) || diff <= tolerance * Math.abs(other)
      }
    }
  }
}
