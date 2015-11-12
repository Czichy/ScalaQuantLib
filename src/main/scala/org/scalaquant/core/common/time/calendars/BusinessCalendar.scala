package org.scalaquant.core.common.time.calendars


import java.util.concurrent.ConcurrentSkipListSet

import org.joda.time.DateTimeConstants._
import org.joda.time.LocalDate

import org.joda.time.Days

import org.scalaquant.core.common.time.TimeUnit._
import org.scalaquant.core.common.time.BusinessDayConvention
import org.scalaquant.core.common.time.BusinessDayConvention._


trait BusinessCalendar {

  protected def considerBusinessDayShadow(implicit date: LocalDate): Boolean

  def name: String
  protected val addedHolidays: java.util.Set[LocalDate] =  new ConcurrentSkipListSet[LocalDate]()
  protected val removedHolidays: java.util.Set[LocalDate] = new ConcurrentSkipListSet[LocalDate]()

  def isWeekend(date: LocalDate): Boolean

  def considerBusinessDay(date: LocalDate): Boolean = {
    if (addedHolidays.contains(date)) {
      false
    } else {
      if (removedHolidays.contains(date)){
        true
      } else {
        considerBusinessDayShadow(date)
      }
    }
  }

  def considerHoliday(date: LocalDate): Boolean = !considerBusinessDay(date)

  def isEndOfMonth(date: LocalDate): Boolean = date.getMonthOfYear != adjust(date.plusDays(1)).getMonthOfYear
  def endOfMonth(date: LocalDate): LocalDate =  adjust(date.dayOfMonth().withMaximumValue(), Preceding)

  def addHoliday(date: LocalDate): Unit = {
    addedHolidays.add(date)
  }
  def removeHoliday(date: LocalDate): Unit = {
    removedHolidays.add(date)
  }


  def adjust(date: LocalDate, convention: BusinessDayConvention = Following): LocalDate = {

    val nextBusinessDay = Stream.from(0).map(date.plusDays).find(considerBusinessDay).get
    val priorBusinessDay = Stream.from(0).map(date.minusDays).find(considerBusinessDay).get

    def intoNextMonth = nextBusinessDay.getMonthOfYear != date.getMonthOfYear
    def intoPrevMonth = priorBusinessDay.getMonthOfYear != date.getMonthOfYear
    def passedMidMonth = date.getDayOfMonth <= 15 && nextBusinessDay.getDayOfMonth > 15

    convention match {
      case Unadjusted => date
      case Following => nextBusinessDay
      case ModifiedFollowing => if (intoNextMonth) adjust(date, Preceding) else nextBusinessDay
      case HalfMonthModifiedFollowing => if (passedMidMonth || intoNextMonth) adjust(date, Preceding) else nextBusinessDay
      case Preceding => priorBusinessDay
      case ModifiedPreceding => if (intoPrevMonth) adjust(date, Following) else priorBusinessDay
    }
  }

  def advance(date: LocalDate, n: Int, unit: TimeUnit,
              convention: BusinessDayConvention = Following, endOfMonth: Boolean = false): LocalDate = {

    if (n == 0) {
       adjust(date, convention)
    } else {
      unit match {
        case Days => adjust(date.plusDays(n), convention)
        case Weeks => adjust(date.plusWeeks(n), convention)
        case Months | Years =>
          val advancedDate = if (unit == Months) date.plusMonths(n) else date.plusYears(n)
          if (endOfMonth && isEndOfMonth(date)) this.endOfMonth(advancedDate) else adjust(advancedDate, convention)
      }
    }
  }

  private def dayRanges(from: LocalDate, to: LocalDate,
                         includeFirst: Boolean = true, includeLast: Boolean = false) ={
    val isPositiveFlow = from < to

    def adjustment(date: LocalDate, offset: Int) = if (isPositiveFlow) date.plusDays(offset) else date.minusDays(offset)

    val startDate = if (includeFirst) from else adjustment(from, 1)
    val endDate = if (includeLast) to else adjustment(to, -1)

    //val range = (0 to Days.daysBetween(startDate, endDate).getDays)
    //def inRange(date: LocalDate) = if (isPositiveFlow) date <= endDate else date >= endDate

    Stream.from(0, Days.daysBetween(startDate, endDate).getDays).map(adjustment(startDate, _))
  }

  def holidays(from: LocalDate, to: LocalDate, includeWeekEnd: Boolean = false): List[LocalDate] = {
    def isHoliday(date: LocalDate) = considerHoliday(date) && (includeWeekEnd || !isWeekend(date))
    dayRanges(from, to, includeFirst = true, includeLast = true).filter(isHoliday).toList
  }

  def businessDaysBetween(from: LocalDate, to: LocalDate,
                          includeFirst: Boolean = true, includeLast: Boolean = false): Int = {
    dayRanges(from, to, includeFirst, includeLast).count(considerBusinessDay)
  }

  override def equals(other: Any) = other match{
    case that: BusinessCalendar => this.name == that.name
  }
}

trait WeekEndSatSun {
  self: BusinessCalendar =>
  def isWeekend(date: LocalDate): Boolean = date.getDayOfWeek match {
      case SUNDAY | SATURDAY => true
      case _ => false
    }
}

trait WeekEndThursFri {
  self: BusinessCalendar =>
  def isWeekend(date: LocalDate): Boolean = date.getDayOfWeek match {
    case THURSDAY | FRIDAY => true
    case _ => false
  }
}
object BusinessCalendar{

  trait Market

  object Western {
  private val EasterMonday = Array(
    98,  90, 103,  95, 114, 106,  91, 111, 102,   // 1901-1909
    87, 107,  99,  83, 103,  95, 115,  99,  91, 111,   // 1910-1919
    96,  87, 107,  92, 112, 103,  95, 108, 100,  91,   // 1920-1929
    111,  96,  88, 107,  92, 112, 104,  88, 108, 100,   // 1930-1939
    85, 104,  96, 116, 101,  92, 112,  97,  89, 108,   // 1940-1949
    100,  85, 105,  96, 109, 101,  93, 112,  97,  89,   // 1950-1959
    109,  93, 113, 105,  90, 109, 101,  86, 106,  97,   // 1960-1969
    89, 102,  94, 113, 105,  90, 110, 101,  86, 106,   // 1970-1979
    98, 110, 102,  94, 114,  98,  90, 110,  95,  86,   // 1980-1989
    106,  91, 111, 102,  94, 107,  99,  90, 103,  95,   // 1990-1999
    115, 106,  91, 111, 103,  87, 107,  99,  84, 103,   // 2000-2009
    95, 115, 100,  91, 111,  96,  88, 107,  92, 112,   // 2010-2019
    104,  95, 108, 100,  92, 111,  96,  88, 108,  92,   // 2020-2029
    112, 104,  89, 108, 100,  85, 105,  96, 116, 101,   // 2030-2039
    93, 112,  97,  89, 109, 100,  85, 105,  97, 109,   // 2040-2049
    101,  93, 113,  97,  89, 109,  94, 113, 105,  90,   // 2050-2059
    110, 101,  86, 106,  98,  89, 102,  94, 114, 105,   // 2060-2069
    90, 110, 102,  86, 106,  98, 111, 102,  94, 114,   // 2070-2079
    99,  90, 110,  95,  87, 106,  91, 111, 103,  94,   // 2080-2089
    107,  99,  91, 103,  95, 115, 107,  91, 111, 103,   // 2090-2099
    88, 108, 100,  85, 105,  96, 109, 101,  93, 112,   // 2100-2109
    97,  89, 109,  93, 113, 105,  90, 109, 101,  86,   // 2110-2119
    106,  97,  89, 102,  94, 113, 105,  90, 110, 101,   // 2120-2129
    86, 106,  98, 110, 102,  94, 114,  98,  90, 110,   // 2130-2139
    95,  86, 106,  91, 111, 102,  94, 107,  99,  90,   // 2140-2149
    103,  95, 115, 106,  91, 111, 103,  87, 107,  99,   // 2150-2159
    84, 103,  95, 115, 100,  91, 111,  96,  88, 107,   // 2160-2169
    92, 112, 104,  95, 108, 100,  92, 111,  96,  88,   // 2170-2179
    108,  92, 112, 104,  89, 108, 100,  85, 105,  96,   // 2180-2189
    116, 101,  93, 112,  97,  89, 109, 100,  85, 105    // 2190-2199
    )
  def easterMonday(year: Int): Int = EasterMonday(year-1901)
}

object GeneralHolidays{

  def inDecember(implicit date: LocalDate) = date.getMonthOfYear == DECEMBER

  def inJanuary(implicit date: LocalDate) = date.getMonthOfYear == JANUARY

  def inFebruary(implicit date: LocalDate) = date.getMonthOfYear == FEBRUARY

  def inJune(implicit date: LocalDate) = date.getMonthOfYear == JUNE

  def inJuly(implicit date: LocalDate) = date.getMonthOfYear == JULY

  def inApril(implicit date: LocalDate) = date.getMonthOfYear == APRIL

  def inAugust(implicit date: LocalDate) = date.getMonthOfYear == AUGUST

  def inSeptember(implicit date: LocalDate) = date.getMonthOfYear == SEPTEMBER

  def inOctober(implicit date: LocalDate) = date.getMonthOfYear == OCTOBER

  def inNovember(implicit date: LocalDate) = date.getMonthOfYear == NOVEMBER

  def inMay(implicit date: LocalDate) = date.getMonthOfYear == MAY

  def isNewYear(implicit date: LocalDate) = date.getDayOfMonth == 1 && inJanuary

  def isNewYearOnMonday(implicit date: LocalDate) = date.getDayOfMonth == 2 && isMonday && inJanuary

  def isMonday(implicit date: LocalDate) = date.getDayOfWeek == MONDAY

  def isTuesDay(implicit date: LocalDate) = date.getDayOfWeek == TUESDAY

  def isFriday(implicit date: LocalDate) = date.getDayOfWeek == FRIDAY

  def isFirstMonday(implicit date: LocalDate) = date.getDayOfMonth <= 7 && isMonday

  def isSecondMonday(implicit date: LocalDate) = {
    val dom = date.getDayOfMonth
    (dom > 7 && dom <= 14) && isMonday
  }

  def isChristmas(implicit date: LocalDate) = {
      val dom = date.getDayOfMonth
      (dom == 25 || dom == 27 && (isMonday || isTuesDay)) && inDecember
  }

  def isBoxingDay(implicit date: LocalDate) = {
      val dom = date.getDayOfMonth
      (dom == 26 || dom == 28 && (isMonday || isTuesDay)) && inDecember
  }

  def isGoodFriday(implicit date: LocalDate) = date.getDayOfYear == Western.easterMonday(date.getYear) - 3

  def isEasterMonday(implicit date: LocalDate) = date.getDayOfYear == Western.easterMonday(date.getYear)

}
  object Orthodox{
  private val EasterMonday = Array(
    105, 118, 110, 102, 121, 106, 126, 118, 102,   // 1901-1909
    122, 114,  99, 118, 110,  95, 115, 106, 126, 111,   // 1910-1919
    103, 122, 107,  99, 119, 110, 123, 115, 107, 126,   // 1920-1929
    111, 103, 123, 107,  99, 119, 104, 123, 115, 100,   // 1930-1939
    120, 111,  96, 116, 108, 127, 112, 104, 124, 115,   // 1940-1949
    100, 120, 112,  96, 116, 108, 128, 112, 104, 124,   // 1950-1959
    109, 100, 120, 105, 125, 116, 101, 121, 113, 104,   // 1960-1969
    117, 109, 101, 120, 105, 125, 117, 101, 121, 113,   // 1970-1979
    98, 117, 109, 129, 114, 105, 125, 110, 102, 121,   // 1980-1989
    106,  98, 118, 109, 122, 114, 106, 118, 110, 102,   // 1990-1999
    122, 106, 126, 118, 103, 122, 114,  99, 119, 110,   // 2000-2009
    95, 115, 107, 126, 111, 103, 123, 107,  99, 119,   // 2010-2019
    111, 123, 115, 107, 127, 111, 103, 123, 108,  99,   // 2020-2029
    119, 104, 124, 115, 100, 120, 112,  96, 116, 108,   // 2030-2039
    128, 112, 104, 124, 116, 100, 120, 112,  97, 116,   // 2040-2049
    108, 128, 113, 104, 124, 109, 101, 120, 105, 125,   // 2050-2059
    117, 101, 121, 113, 105, 117, 109, 101, 121, 105,   // 2060-2069
    125, 110, 102, 121, 113,  98, 118, 109, 129, 114,   // 2070-2079
    106, 125, 110, 102, 122, 106,  98, 118, 110, 122,   // 2080-2089
    114,  99, 119, 110, 102, 115, 107, 126, 118, 103,   // 2090-2099
    123, 115, 100, 120, 112,  96, 116, 108, 128, 112,   // 2100-2109
    104, 124, 109, 100, 120, 105, 125, 116, 108, 121,   // 2110-2119
    113, 104, 124, 109, 101, 120, 105, 125, 117, 101,   // 2120-2129
    121, 113,  98, 117, 109, 129, 114, 105, 125, 110,   // 2130-2139
    102, 121, 113,  98, 118, 109, 129, 114, 106, 125,   // 2140-2149
    110, 102, 122, 106, 126, 118, 103, 122, 114,  99,   // 2150-2159
    119, 110, 102, 115, 107, 126, 111, 103, 123, 114,   // 2160-2169
    99, 119, 111, 130, 115, 107, 127, 111, 103, 123,   // 2170-2179
    108,  99, 119, 104, 124, 115, 100, 120, 112, 103,   // 2180-2189
    116, 108, 128, 119, 104, 124, 116, 100, 120, 112    // 2190-2199
  )
  def easterMonday(year: Int): Int = EasterMonday(year-1901)
}
}