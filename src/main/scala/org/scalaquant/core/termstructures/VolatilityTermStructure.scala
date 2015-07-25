package org.scalaquant.core.termstructures

import org.joda.time.LocalDate
import org.scalaquant.common.time.{TimeUnit, Period, BusinessDayConvention}
import org.scalaquant.common.time.calendars.BusinessCalendar
import org.scalaquant.common.time.daycounts.DayCountConvention


abstract class VolatilityTermStructure(private var _referenceDate: LocalDate,
                              override val calendar: BusinessCalendar,
                              override val dc: DayCountConvention,
                              bdc: BusinessDayConvention ) extends TermStructure(_referenceDate, calendar, dc){
//  private def checkStrike(k: Double, extrapolate: Boolean): Boolean = {
//    extrapolate || allowsExtrapolation || (k >= minStrike() && k <= maxStrike())
//  }

  def optionDateFromTenor(p: Period) = {
    calendar.advance(referenceDate, p.days.toInt, TimeUnit.Days, bdc )
  }
}
