package org.scalaquant.core.common.time

/**
 * Created by neo on 2015-03-02.
 */
sealed trait BusinessDayConvention

object BusinessDayConvention {
  case object Following extends BusinessDayConvention
  case object ModifiedFollowing extends BusinessDayConvention
  case object Preceding extends BusinessDayConvention
  case object ModifiedPreceding extends BusinessDayConvention
  case object Unadjusted extends BusinessDayConvention
  case object HalfMonthModifiedFollowing extends BusinessDayConvention
}
