package org.scalaquant.core.common.time.daycounts

import org.joda.time.LocalDate
import org.joda.time.DateTimeConstants._

import org.scalaquant.core.common.time.JodaDateTimeHelper._

object Actual365NoLeap {

  private def S(date: LocalDate) = {
    val s = date.getDayOfMonth + MonthOffset(date.getMonthOfYear - 1) + (date.getYear * 365)
    if (date.getMonthOfYear == FEBRUARY && date.getDayOfMonth == 29) s - 1 else s
  }

  private val actual365NoLeapImpl = new DayCountConvention {
    val name: String = "Actual/365 (NL)"

    override def dayCount(date1: LocalDate, date2: LocalDate): Int = S(date2) - S(date1)

    def fractionOfYear(date1: LocalDate,
                       date2: LocalDate,
                       refDate1: Option[LocalDate] = None,
                       refDate2: Option[LocalDate] = None): Double = dayCount(date1, date2) / 365.0

  }

  def apply(): DayCountConvention = actual365NoLeapImpl
}
