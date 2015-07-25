package org.scalaquant.core.termstructures

import org.joda.time.LocalDate
import org.scalaquant.common.Settings
import org.scalaquant.common.time.TimeUnit
import org.scalaquant.common.time.calendars.BusinessCalendar
import org.scalaquant.common.time.daycounts.DayCountConvention
import rx.lang.scala.Subject

abstract class TermStructure[T] extends Subject[T] {
  private var moving = false
  protected var _settlementDays: Int = 0


  def this(dc: DayCountConvention) = {

  }
  def this(referenceDate: LocalDate, calendar: BusinessCalendar, dc: DayCountConvention) = {

  }
  def this(settlementDays: Int, calendar: BusinessCalendar, dc: DayCountConvention)  = {
    this(Settings.evaluationDate, calendar, dc)
    this.moving = true
    this._settlementDays = settlementDays
  }

//  protected def checkRage(date: LocalDate, extrapolate: Boolean): Unit = {
//    require(date >= _referenceDate, "")
//  }

  def maxDate: LocalDate

  def maxTime: Double = timeFromReference(maxDate)

  def timeFromReference(date: LocalDate): Double = dc.fractionOfYear(referenceDate, date , date)

  def settlementDays: Int = _settlementDays

  def referenceDate: LocalDate = {
    _settlementDays.fold[LocalDate](_referenceDate){ settlementDays =>
      calendar.advance(Settings.evaluationDate, settlementDays, TimeUnit.Days)
    }
  }

}
