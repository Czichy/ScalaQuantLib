package org.scalaquant.core.termstructures

import org.joda.time.{Period, LocalDate}
import org.scalaquant.core.common.time.Frequency
import org.scalaquant.core.common.time.Frequency._
import org.scalaquant.core.common.time.daycounts.DayCountConvention

package object inflation {

    def period(date: LocalDate, frequency: Frequency): (LocalDate, LocalDate) = {
      val month = date.getMonthOfYear
      val year = date.getYear
      def result(startMonth:Int, endMonth: Int) = {
        (new LocalDate(year, startMonth, 1), new LocalDate(year, endMonth,1).dayOfMonth.withMaximumValue)
      }
       frequency match {
        case Annual => result(1,12)
        case Semiannual =>
          val startMonth = 6 * ( (month-1) / 6 ) + 1
          val endMonth = startMonth + 5
          result(startMonth, endMonth)
        case Quarterly =>
          val startMonth = 3 * ( (month-1) / 3 ) + 1
          val endMonth = startMonth + 2
          result(startMonth, endMonth)
        case Monthly =>
          result(month, month)
        case _ =>
          (date, date)
      }

    }

    def fractionOfYear(f: Frequency,
                       indexIsInterpolated: Boolean,
                       dayCounter: DayCountConvention,
                       d1: LocalDate,
                       d2: LocalDate): Double = {

      val (date1, date2, date3) = if (indexIsInterpolated)
        (d1, d2, d2)
      else
        (period(d1, f)._1, period(d2, f)._1, period(d2, f)._1)

      dayCounter.fractionOfYear(date1, date2, date3, f)
    }
}
