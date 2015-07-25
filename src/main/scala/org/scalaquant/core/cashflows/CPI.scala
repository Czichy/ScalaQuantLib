package org.scalaquant.core.cashflows

object CPI {
  sealed trait InterpolationType
  case object AsIndex extends InterpolationType   //!< same interpolation as index
  case object Flat extends InterpolationType     //!< flat from previous fixing
  case object Linear extends InterpolationType    //!< linearly between bracketing fixings
}
