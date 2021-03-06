package org.scalaquant.core.common.time.calendars

import java.util.concurrent.ConcurrentSkipListSet

import java.time.LocalDate

class BespokeCalendar(override val name: String) extends BusinessCalendar{

  private val weekends: ConcurrentSkipListSet[LocalDate] = new ConcurrentSkipListSet[LocalDate]()

  protected def considerBusinessDayShadow(implicit date: LocalDate): Boolean = {
    !isWeekend(date)
  }

  override def isWeekend(date: LocalDate): Boolean = {
    weekends.contains(date)
  }

  def addWeekend(date: LocalDate): Unit = {
    weekends.add(date)
  }
}
