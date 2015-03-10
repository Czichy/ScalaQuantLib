package org.scalaquant.core.indexes


import org.joda.time.LocalDate
import org.scalaquant.core.common.TimeSeries
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import rx.lang.scala.Observable

/**
 * Created by neo on 2015-03-02.
 */
trait Index[Double] extends Observable[Double]{
  def name: String
  def fixingCalendar: BusinessCalendar
  def isValidFixingDate(fixingDate: LocalDate): Boolean
  def fixing(fixingDate: LocalDate, forecastTodaysFixing: Boolean = false)
  def timeSeries: TimeSeries[LocalDate, Double]
  def clearAllFixings(): Unit
}
