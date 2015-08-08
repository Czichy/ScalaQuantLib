package org.scalaquant.common.time.daycounts

import org.joda.time.LocalDate

object Actual364 {

  private val actual364Impl = new DayCountConvention {

    val name: String = "Actual/364"

    def fractionOfYear(date1: LocalDate,
                       date2: LocalDate,
                       refDate1: Option[LocalDate] = None,
                       refDate2: Option[LocalDate] = None): Double = dayCount(date1, date2).toDouble / 364.0

  }

  def apply(): DayCountConvention = actual364Impl
}
