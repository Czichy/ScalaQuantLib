package org.scalaquant.core.instruments.equities

object Split

case class SplitRatio(n: Int, m: Int) {
  override def toString = s"$n for $m"
}