package org.scalaquant.core.termstructures

import org.joda.time.LocalDate

import org.scalaquant.common.time.calendars.BusinessCalendar
import org.scalaquant.common.time.daycounts.DayCountConvention
import org.scalaquant.core.types.YearFraction

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._
import org.scalaquant.math.interpolations.Extrapolator

abstract class TermStructure(val settlementDays: Int,
                             val referenceDate: LocalDate,
                             val calendar: BusinessCalendar,
                             val dc: DayCountConvention) extends Extrapolator {

  def maxDate: LocalDate

  def maxTime: YearFraction = timeFromReference(maxDate)

  def timeFromReference(date: LocalDate): Double = dc.fractionOfYear(referenceDate, date)

  protected def checkRange(date: LocalDate, extrapolate: Boolean) = {
    require(date >= referenceDate, s"date ($date) before reference date ($referenceDate)")
    require(extrapolate || allowsExtrapolation || date <= maxDate,
            s"date ($date) is past max curve date ($maxDate)")
  }

  protected def checkRange(time: YearFraction, extrapolate: Boolean) = {
    require(time >= 0.0, s"negative ($time) given")
    require(extrapolate || allowsExtrapolation || time <= maxTime,
       s"time ($time) is past max curve time ($maxTime)")
  }

}
