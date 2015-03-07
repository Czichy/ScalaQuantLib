package org.scalaquant.core.common.time

import org.joda.time.{ DateTime, LocalTime, LocalDate }
import com.github.nscala_time.time.OrderingImplicits.LocalDateOrdering
/**
 * Created by neo on 2015-03-02.
 */
object JodaDateTimeHelper {
  implicit class JodaDateWrapper(val date: LocalDate) extends AnyVal {
    def ==(otherDate: LocalDate): Boolean = date.isEqual(otherDate)
    def !=(otherDate: LocalDate): Boolean = !date.isEqual(otherDate)
    def <=(otherDate: LocalDate): Boolean = date.isEqual(otherDate) || date.isBefore(otherDate)
    def <(otherDate: LocalDate): Boolean = date.isBefore(otherDate)
    def >(otherDate: LocalDate): Boolean = date.isAfter(otherDate)
    def >=(otherDate: LocalDate): Boolean = date.isEqual(otherDate) || date.isAfter(otherDate)
    def isEndOfMoth: Boolean = date.dayOfMonth.withMaximumValue().isEqual(date)
    def inLeapYear: Boolean = date.year.isLeap
    def YMD: (Int, Int, Int) = (date.getYear, date.getMonthOfYear, date.getDayOfMonth)

  }
  implicit class JodaTimeWrapper(val time: LocalTime) extends AnyVal {
    def ==(otherTime: LocalTime): Boolean = time.isEqual(otherTime)
    def !=(otherTime: LocalTime): Boolean = !time.isEqual(otherTime)
    def <=(otherTime: LocalTime): Boolean = time.isEqual(otherTime) || time.isBefore(otherTime)
    def <(otherTime: LocalTime): Boolean = time.isBefore(otherTime)
    def >(otherTime: LocalTime): Boolean = time.isAfter(otherTime)
    def >=(otherTime: LocalTime): Boolean = time.isEqual(otherTime) || time.isAfter(otherTime)
  }

  implicit class JodaDateTimeWrapper(val time: DateTime) extends AnyVal {
    def ==(otherTime: DateTime): Boolean = time.isEqual(otherTime)
    def !=(otherTime: DateTime): Boolean = !time.isEqual(otherTime)
    def <=(otherTime: DateTime): Boolean = time.isEqual(otherTime) || time.isBefore(otherTime)
    def <(otherTime: DateTime): Boolean = time.isBefore(otherTime)
    def >(otherTime: DateTime): Boolean = time.isAfter(otherTime)
    def >=(otherTime: DateTime): Boolean = time.isEqual(otherTime) || time.isAfter(otherTime)
  }

  def min(date1: LocalDate, date2: LocalDate): LocalDate = Set(date1, date2).min

  val MonthOffset = List(
    0, 31, 59, 90, 120, 151, // Jan - Jun
    181, 212, 243, 273, 304, 334 // Jun - Dec
  )
  val MonthLeapOffset = List(
    0, 31, 60, 91, 121, 152, // Jan - Jun
    182, 213, 244, 274, 305, 335 // Jun - Dec
  )
}
