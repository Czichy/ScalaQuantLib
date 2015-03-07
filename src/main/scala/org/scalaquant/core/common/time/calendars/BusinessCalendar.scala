package org.scalaquant.core.common.time.calendars

/**
 * Created by neo on 2015-03-06.
 */

import org.joda.time.{Period, DurationFieldType, LocalDate}
import org.scalaquant.core.common.time.BusinessDayConvention
import org.scalaquant.core.common.time.BusinessDayConvention.Following

trait BusinessCalendar {
  def isEmpty: Boolean

  def name: String

  def considerBusinessDay(date: LocalDate): Boolean

  def considerHoliday(date: LocalDate): Boolean

//  bool isWeekend(Weekday w) const;
//  /*! Returns <tt>true</tt> iff the date is last business day for the
//      month in given market.
//  */
//  bool isEndOfMonth(const Date& d) const;
//  //! last business day of the month to which the given date belongs
//  Date endOfMonth(const Date& d) const;

  /*! Adds a date to the set of holidays for the given calendar. */
  def addHoliday(date: LocalDate)(): Unit
  /*! Removes a date from the set of holidays for the given calendar. */
  def removeHoliday(date: LocalDate): Unit

  def holidays(from: LocalDate, to: LocalDate, includeWeekEnd: Boolean = false): List[LocalDate]
  //! Returns the holidays between two dates

  /*! Adjusts a non-business day to the appropriate near business day
      with respect to the given convention.
  */
  def adjust(date: LocalDate, convention: BusinessDayConvention = Following): LocalDate
  /*! Advances the given date of the given number of business days and
      returns the result.

  */
  def advance(date: LocalDate, n: Int, unit: DurationFieldType,
              convention: BusinessDayConvention = Following, endOfMonth: Boolean = false): LocalDate
  /*! Advances the given date as specified by the given period and
      returns the result.
      \note The input date is not modified.
  */
  def advance(date: LocalDate, period: Period,
              convention: BusinessDayConvention = Following, endOfMonth: Boolean = false): LocalDate
  /*! Calculates the number of business days between two given
      dates and returns the result.
  */
  def businessDaysBetween(from: LocalDate, to: LocalDate,
                          includeFirst: Boolean = true, includeLast: Boolean = false): Int
  //@}

}
