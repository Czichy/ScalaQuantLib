package org.scalaquant.common.time


sealed trait BusinessDayConvention

object BusinessDayConvention {
  case object Following extends BusinessDayConvention
  case object ModifiedFollowing extends BusinessDayConvention
  case object Preceding extends BusinessDayConvention
  case object ModifiedPreceding extends BusinessDayConvention
  case object Unadjusted extends BusinessDayConvention
  case object HalfMonthModifiedFollowing extends BusinessDayConvention
}
