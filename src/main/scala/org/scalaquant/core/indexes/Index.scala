package org.scalaquant.core.indexes


import org.joda.time.LocalDate
import org.scalaquant.core.common.TimeSeries
import org.scalaquant.core.common.time.calendars.BusinessCalendar



trait Index{
  def name: String
  def fixingCalendar: BusinessCalendar
  def isValidFixingDate(fixingDate: LocalDate): Boolean
  def fixing(fixingDate: LocalDate, forecastTodaysFixing: Boolean = false): Option[Double]
  def timeSeries: TimeSeries[LocalDate, Double]
  def clearAllFixings(): Unit
}
