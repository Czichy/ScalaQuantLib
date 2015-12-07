package org.scalaquant.core.common.time

import org.joda.time.{Days, LocalDate}
import org.scalaquant.core.common.time.DayOfWeek.DayOfWeek

object JodaDateTimeHelper {

  def firstDayOf(month: Int, year: Int): LocalDate = {
    LocalDate.now.withDayOfMonth(1).withMonthOfYear(month).withYear(year)
  }

  def lastDayOf(month: Int, year: Int): LocalDate = {
    firstDayOf(month,year).plusMonths(1).plusDays(-1)
  }


  implicit class JodaDateWrapper(val date: LocalDate) extends AnyVal {

    def isEndOfMoth: Boolean = date.dayOfMonth.withMaximumValue().isEqual(date)

    def inLeapYear: Boolean = date.year.isLeap

    def YMD: (Int, Int, Int) = (date.getYear, date.getMonthOfYear, date.getDayOfMonth)

    def -(period: Period) = date.plusDays(-period.days.toInt)

    def -(that: LocalDate): Int = Days.daysBetween(date, that).getDays

    def +(period: Period) = date.plusDays(period.days.toInt)
  }

  def min(date1: LocalDate, date2: LocalDate): LocalDate = {
    if (date1.isEqual(date2)) {
      date1
    } else if (date1.isBefore(date2)) {
      date1
    } else {
      date2
    }
  }

  def max(date1: LocalDate, date2: LocalDate): LocalDate = {
    if (date1.equals(min(date1,date2))) date2 else date1
  }

  def nthWeekday(nth:Int, dayOfWeek: DayOfWeek, month: Int, year: Int): LocalDate = {
    require( nth>0 && nth < 6, "no more than 5 weekdays for given month year")

    val first = new LocalDate(year, month, 1).getDayOfWeek
    val skip = if (dayOfWeek.id >= first) nth - 1 else nth

    new LocalDate(year, month, (1 + dayOfWeek.id + skip * 7) - first )
  }

  val theBeginningOfTime = LocalDate.now.year.withMinimumValue
  val farFuture = LocalDate.now.year.withMaximumValue

  val MonthOffset = List(
    0, 31, 59, 90, 120, 151, // Jan - Jun
    181, 212, 243, 273, 304, 334 // Jun - Dec
  )
  val MonthLeapOffset = List(
    0, 31, 60, 91, 121, 152, // Jan - Jun
    182, 213, 244, 274, 305, 335 // Jun - Dec
  )
}
