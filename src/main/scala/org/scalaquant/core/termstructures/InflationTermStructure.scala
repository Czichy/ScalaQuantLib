package org.scalaquant.core.termstructures


import org.joda.time.{DateTimeConstants, LocalDate}
import org.scalaquant.core.common.time.Frequency._
import DateTimeConstants._
import org.scalaquant.core.common.time.Period
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.termstructures.inflation.Seasonality
import org.scalaquant.core.types._

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

abstract class InflationTermStructure(val baseRate: Rate,
                                      val observationLag: Period,
                                      val frequency: Frequency,
                                      val indexIsInterpolated: Boolean,
                                      val yTS: YieldTermStructure,
                                      val seasonality: Seasonality,
                                      settlementDays: Int,
                                      referenceDate: LocalDate,
                                      calendar: BusinessCalendar,
                                      dc: DayCountConvention,
                                      val allowsExtrapolation: Boolean = false)
  extends TermStructure(settlementDays, referenceDate, calendar, dc){

  def baseDate: LocalDate

  override def checkRange(asOf: LocalDate, extrapolate: Boolean) = {
    require(asOf >= baseDate, s"date ($asOf) is before base date")
    require(extrapolate || allowsExtrapolation || asOf <= maxDate, s"date ($asOf) is past max curve date ($maxDate)")
  }

  override def checkRange(time: YearFraction, extrapolate: Boolean) = {
    require(time >= timeFromReference(baseDate), s"time ($time) is before base date")
    require(extrapolate || allowsExtrapolation || time <= maxTime, s"time ($time) is past max curve time ($maxTime)")
  }
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