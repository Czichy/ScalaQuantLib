package org.scalaquant.common.time.daycounts

import org.joda.time.{Days, LocalDate}
import org.scalaquant.common.time.Frequency._
import org.scalaquant.core.types.YearFraction
import org.scalaquant.math.Comparison.Equality
/**
 * Day count convention.
 * It provide methods for determining the length of a time period according to given market convention,
 * both as a number of days and as a year fraction.
 */

trait DayCountConvention {

   def name: String

  def dayCount(startDate: LocalDate, endDate: LocalDate): Int = Days.daysBetween(startDate, endDate).getDays

  //regular fraction
  def fractionOfYear(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency = Annual): YearFraction

  //irreuglar fraction
  def fractionOfYear(date1: LocalDate, date2: LocalDate, refDate1: LocalDate, refDate2: LocalDate): YearFraction

}


object DayCountConvention{

  implicit object DayCountConventionEquality extends Equality[DayCountConvention] {
    def ==(x: DayCountConvention, y: DayCountConvention): Boolean = x.name == y.name
  }

}


