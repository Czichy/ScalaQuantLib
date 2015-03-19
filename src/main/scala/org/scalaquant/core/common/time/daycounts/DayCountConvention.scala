package org.scalaquant.core.common.time.daycounts

import org.joda.time.{ Days, LocalDate }
import org.scalaquant.core.common.Settings
import org.scalaquant.core.common.time.Frequency

/**
 * day count convention trait.
 * It provide methods for determining the length of a time period according to given market convention, both as a number
 * of days and as a year fraction.
 *
 *
 */

trait DayCountConvention {

  def name: String

  def dayCount(startDate: LocalDate, endDate: LocalDate): Int = Days.daysBetween(startDate, endDate).getDays

  def fraction(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency = Frequency.Annual): Double

  override def equals(other: Any): Boolean = other match {
    case that: DayCountConvention => this.name == that.name
    case _ => false
  }

  override def toString: String = name
}



