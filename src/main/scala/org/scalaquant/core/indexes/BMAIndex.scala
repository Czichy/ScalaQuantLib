package org.scalaquant.core.indexes

import java.time.LocalDate
import org.scalaquant.core.common.Compounding
import org.scalaquant.core.common.time.calendars.UnitedStates.NYSE
import org.scalaquant.core.common.time.daycounts.ActualActual.ISDA
import org.scalaquant.core.common.time.{Period, TimeUnit}
import org.scalaquant.core.common.time.calendars.{BusinessCalendar, UnitedStates}
import org.scalaquant.core.common.time.daycounts.{DayCountConvention, ActualActual}
import org.scalaquant.core.termstructures.YieldTermStructure
import org.scalaquant.core.currencies.{Currency, America}
import org.scalaquant.core.types.Rate
import org.scalaquant.core.common.time.JodaDateTimeHelper._

//! Bond Market Association index
/*! The BMA index is the short-term tax-exempt reference index of
    the Bond Market Association.  It has tenor one week, is fixed
    weekly on Wednesdays and is applied with a one-day's fixing
    gap from Thursdays on for one week.  It is the tax-exempt
    correspondent of the 1M USD-Libor.
*/

case class BMAIndex(forwardingTermStructure: YieldTermStructure,
                    familyName: String = "BMA",
                    tenor: Period = Period(1, TimeUnit.Weeks),
                    fixingDays: Int = 1,
                    currency: Currency =America.USD,
                    fixingCalendar: BusinessCalendar = UnitedStates(NYSE),
                    dayCounter: DayCountConvention = ActualActual(ISDA))
  extends InterestRateIndex{

  override def name = familyName

  override def isValidFixingDate(fixingDate: LocalDate): Boolean = {
    if (BusinessCalendar.dayRanges(previousWednesday(fixingDate), fixingDate).exists(fixingCalendar.considerBusinessDay)) false
    else fixingCalendar.considerBusinessDay(fixingDate)
  }

  def maturityDate(valueDate: LocalDate): LocalDate = {
    val fixingDate = fixingCalendar.advance(valueDate, -1, TimeUnit.Days)
    fixingCalendar.advance(nextWednesday(fixingDate), 1, TimeUnit.Days)
  }

  def forecastFixing(fixingDate: LocalDate): Rate = {
    val start = fixingCalendar.advance(fixingDate, 1, TimeUnit.Days)
    val end = maturityDate(start)

    forwardingTermStructure.forwardRate(start, end, this.dayCounter, Compounding.Simple).rate
  }
}
