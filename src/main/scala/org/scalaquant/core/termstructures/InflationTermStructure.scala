package org.scalaquant.core.termstructures

import java.time.Month

import org.joda.time.{DateTimeConstants, LocalDate}
import org.scalaquant.common.time.Frequency._
import DateTimeConstants._
import org.scalaquant.common.time.Period
import org.scalaquant.common.time.calendars.BusinessCalendar
import org.scalaquant.common.time.daycounts.DayCountConvention
import org.scalaquant.core.termstructures.inflation.Seasonality
import org.scalaquant.core.types.Rate

class InflationTermStructure( val baseRate: Rate,
                              val observationLag: Period,
                              val frequency: Frequency,
                              val indexIsInterpolated: Boolean,
                              val yTS: YieldTermStructure,
                              val seasonality: Seasonality,
                              override val settlementDays: Int,
                              override val referenceDate: LocalDate,
                              override val calendar: BusinessCalendar,
                              override val dc: DayCountConvention,
                              override val allowsExtrapolation: Boolean = false)
  extends TermStructure(settlementDays, referenceDate, calendar, dc){

}


object InflationTermStructure {

  def inflationPeriod(d: LocalDate,frequency: Frequency): (LocalDate, LocalDate) = {

    val month = d.getMonthOfYear
    val year = d.getYear

    val (startMonth, endMonth) =  frequency match {
      case Annual => (JANUARY, DECEMBER)
      case Semiannual =>
        val startMonth = 6 * ((month - 1)/6) + 1
        (startMonth, startMonth + 5)
      case Quarterly =>
        val startMonth = 3 * ((month-1)/3) + 1
        (startMonth, startMonth + 2)
      case Monthly => (month, month)
      case _ => (0, 0)
    }

    val startDate = new LocalDate(year, startMonth, 1)
    val endDate = new LocalDate(year, endMonth, 1).plusMonths(1).plusDays(-1)

    (startDate, endDate)
  }
}