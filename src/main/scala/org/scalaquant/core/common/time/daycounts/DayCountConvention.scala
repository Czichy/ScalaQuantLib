package org.scalaquant.core.common.time.daycounts

/**
 * Created by neo on 2015-03-02.
 */

import org.joda.time.{ Days, LocalDate }
import org.scalaquant.core.common.time.Frequency

/**
 * Created by neo on 2015-03-01.
 */

trait DayCountConvention {
  def name: String
  def dayCount(startDate: LocalDate, endDate: LocalDate): Int = Days.daysBetween(startDate, endDate).getDays
  def fraction(date1: LocalDate, date2: LocalDate): Double
  def fraction(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency): Double = fraction(date1, date2)

  override def equals(other: Any): Boolean = other match {
    case that: DayCountConvention => this.name == that.name
    case _ => false
  }

}
