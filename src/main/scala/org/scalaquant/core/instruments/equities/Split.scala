package org.scalaquant.core.instruments.equities

object Split

case class SplitRatio(n: Int, m: Int) {
  def description = s"$n for $m"
}