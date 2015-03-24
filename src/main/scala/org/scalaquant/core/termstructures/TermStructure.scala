package org.scalaquant.core.termstructures

import org.joda.time.LocalDate
import org.scalaquant.core.common.Settings
import org.scalaquant.core.common.time.TimeUnit
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import rx.lang.scala.Subject

abstract class TermStructure[T](private var _referenceDate: LocalDate,
                                val calendar: BusinessCalendar,
                                val dc: DayCountConvention) extends Subject[T] {
  private var moving = false
  protected var _settlementDays: Option[Int] = None

  def this(settlementDays: Int, calendar: BusinessCalendar, dc: DayCountConvention)  = {
    this(Settings.evaluationDate, calendar, dc)
    this.moving = true
    this._settlementDays = Option(settlementDays)
  }

//  protected def checkRage(date: LocalDate, extrapolate: Boolean): Unit = {
//    require(date >= _referenceDate, "")
//  }

  def maxDate: LocalDate

  def maxTime: Double = timeFromReference(maxDate)

  def timeFromReference(date: LocalDate): Double = dc.fraction(_referenceDate, date , date)

  def settlementDays: Option[Int] = _settlementDays

  def referenceDate: LocalDate = {
    _settlementDays.fold[LocalDate](_referenceDate){ settlementDays =>
      calendar.advance(Settings.evaluationDate, settlementDays, TimeUnit.Days)
    }
  }

}
