package org.scalaquant.math.interpolations

trait Extrapolator {
  def allowsExtrapolation: Boolean = false
}
