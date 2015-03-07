package org.scalaquant.core.common

/**
 * Created by neo on 2015-02-28.
 */
object Compounding extends Enumeration {
  type Compounding = Value
  val Simple, Compounded, Continuous, SimpleThenCompounded = Value

}
