package org.scalaquant.core.common

object Compounding {
  sealed trait Compounding
  case object Simple extends Compounding
  case object Compounded extends Compounding
  case object Continuous extends Compounding
  case object SimpleThenCompounded extends Compounding
}
