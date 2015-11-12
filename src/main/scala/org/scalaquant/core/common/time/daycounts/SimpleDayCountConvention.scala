package org.scalaquant.core.common.time.daycounts

import org.joda.time.LocalDate
import org.scalaquant.core.types._

import org.scalaquant.core.common.time.JodaDateTimeHelper._

object SimpleDayCountConvention{

  private val simpleImpl = new DayCountConvention  {
    val name: String = "Simple"
    private val fallBack = Thirty360()
    override def dayCount(date1: LocalDate, date2: LocalDate): Int = fallBack.dayCount(date1, date2)

    override def fractionOfYear(date1: LocalDate,
                                date2: LocalDate,
                                refDate1: Option[LocalDate] = None,
                                refDate2: Option[LocalDate] = None): YearFraction = {
      val dm1 = date1.getDayOfMonth
      val dm2 = date2.getDayOfMonth

      if (dm1 == dm2 ||
        // e.g., Aug 30 -> Feb 28 ?
        (dm1 > dm2 && date2.isEndOfMoth) ||
        // e.g., Feb 28 -> Aug 30 ?
        (dm1 < dm2 && date1.isEndOfMoth))
      {
        (date2.getYear - date1.getYear) + (date2.getMonthOfYear - date1.getMonthOfYear) / 12.0
      } else {
        fallBack.fractionOfYear(date1, date2)
      }
    }

  }

  def apply(): DayCountConvention = simpleImpl
}
