package org.scalaquant.core.indexes


import org.joda.time.LocalDate
import org.scalaquant.core.common.TimeSeries
import org.scalaquant.core.common.time.calendars.BusinessCalendar

import scala.collection.convert.Wrappers.MutableMapWrapper

trait Index{

  def name: String
  def fixingCalendar: BusinessCalendar
  def isValidFixingDate(fixingDate: LocalDate): Boolean
  def fixing(fixingDate: LocalDate, forecastTodaysFixing: Boolean = false): Double
  def timeSeries: TimeSeries[LocalDate, Double] = IndexManager.getHistory(name).getOrElse(IndexManager.emptyIndex)

}

trait HistoryFixing{
  self: Index =>
  def clearFixings(): Unit = {
    IndexManager.clearHistory(name)
  }
  def addFixing(date: LocalDate, value: Double, forceOverwrite: Boolean = false) = {
    addFixings(List((date,value)), forceOverwrite)
  }
  def addFixings(fixings: Seq[(LocalDate,Double)], forceOverwrite: Boolean = false) = {
    val (validFixings, invalidFixings) = fixings.partition(x => isValidFixingDate(x._1))
    require(invalidFixings.isEmpty, invalidFixings.headOption.map("At least one invalid fixing provided:"+_._1))
    val fixingMaps = validFixings.toMap
    val history = self.timeSeries
    def valueOf(key: LocalDate) = if (forceOverwrite) fixingMaps(key) else timeSeries.get(key)

    val duplicates = history.dates.intersect(validFixings.map(_._1)).map(date => (date,valueOf(date)))
    val newFixings = validFixings.map(_._1).diff(history.dates).map(date => (date, fixingMaps(date)))

    val newTimeSeries = TimeSeries((duplicates ++ newFixings).sortBy(_._1).toMap)
    IndexManager.setHistory(name, newTimeSeries)
  }
}