package org.scalaquant.common.time.daycounts

import org.joda.time.LocalDate
import org.scalaquant.core.types._

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

object OneDay {

  private val oneImpl = new DayCountConvention {
    val name: String = "1/1"

    override def dayCount(date1: LocalDate, date2: LocalDate): Int = if (date1 > date2) -1 else 1

    def fractionOfYear(date1: LocalDate,
                       date2: LocalDate,
                       refDate1: Option[LocalDate] = None,
                       refDate2: Option[LocalDate] = None): YearFraction = dayCount(date1, date2).toDouble
  }

  def apply(): DayCountConvention = oneImpl
}