package org.scalaquant.core.common.time.daycounts

import org.joda.time.LocalDate

object Actual365Fixed {

  private val actual365Impl = new DayCountConvention {
    val name: String = "Actual/365 (Fixed)"
    def fractionOfYear(date1: LocalDate,
                       date2: LocalDate,
                       refDate1: Option[LocalDate] = None,
                       refDate2: Option[LocalDate] = None): Double = dayCount(date1, date2) / 365.0
  }

  def apply(): DayCountConvention = actual365Impl

}