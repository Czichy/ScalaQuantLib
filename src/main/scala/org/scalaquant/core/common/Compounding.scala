package org.scalaquant.core.common

/**
 * Created by neo on 2015-02-28.
 */
sealed trait Compounding
object Compounding {
  case object Simple extends Compounding
  case object Compounded extends Compounding
  case object Continuous extends Compounding
  case object SimpleThenCompounded extends Compounding
}
