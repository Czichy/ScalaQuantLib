package org.scalaquant.core.common.time.daycounts

import org.joda.time.LocalDate

object Actual360 {

  private val actual360Impl =  new DayCountConvention {

    val name: String = "Actual/360"

    def fractionOfYear(date1: LocalDate,
                       date2: LocalDate,
                       refDate1: Option[LocalDate] = None,
                       refDate2: Option[LocalDate] = None): Double =  dayCount(date1, date2) / 360.0
  }

  def apply(): DayCountConvention = actual360Impl
}