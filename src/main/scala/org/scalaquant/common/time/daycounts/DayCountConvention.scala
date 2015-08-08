package org.scalaquant.common.time.daycounts

import org.joda.time.{Days, LocalDate}
import org.scalaquant.core.types.YearFraction
import org.scalaquant.math.Comparison.{InEquality, Equality}
/**
 * Day count convention.
 * It provide methods for determining the length of a time period according to given market convention,
 * both as a number of days and as a year fraction.
 */

abstract class DayCountConvention {

  def name: String

  def dayCount(startDate: LocalDate, endDate: LocalDate): Int = Days.daysBetween(startDate, endDate).getDays

  def fractionOfYear(date1: LocalDate, date2: LocalDate,
                     refDate1: Option[LocalDate] = None, refDate2: Option[LocalDate] = None): YearFraction

}


object DayCountConvention{

  implicit object DayCountConventionEquality extends Equality[DayCountConvention] {
    def ==(x: DayCountConvention, y: DayCountConvention): Boolean = x.name == y.name
  }

  implicit object DayCountConventionInEquality extends InEquality[DayCountConvention] {
    def !=(x: DayCountConvention, y: DayCountConvention): Boolean = x.name != y.name
  }

}


