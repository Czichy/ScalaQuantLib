package org.scalaquant.core.common.time.daycounts

/**
 * Created by neo on 2015-03-02.
 */

import org.joda.time.{ Days, LocalDate }

/**
 * Created by neo on 2015-03-01.
 */
object DayCountConvention {

  trait Frequency {
    def value: Int
  }
  case class FrequencyValue(value: Int) extends Frequency
  object Frequency {
    val Annual = FrequencyValue(1)
    val SemiAnnual = FrequencyValue(2)
    val Quarterly = FrequencyValue(4)
    val Monthly = FrequencyValue(12)
  }
}

trait DayCountConvention {
  def name: String
  def dayCount(startDate: LocalDate, endDate: LocalDate): Int = Days.daysBetween(startDate, endDate).getDays
  def fraction(date1: LocalDate, date2: LocalDate): Double
  def fraction(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: DayCountConvention.Frequency): Double = fraction(date1, date2)

  def ===(other: DayCountConvention): Boolean = this.name == other.name
}
