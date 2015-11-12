package org.scalaquant.core.termstructures

/**
  * Created by neo on 11/12/15.
  */
package object volatility {

  sealed trait VolatilityType

  case object ShiftedLognormal extends VolatilityType
  case object Normal extends VolatilityType

}
