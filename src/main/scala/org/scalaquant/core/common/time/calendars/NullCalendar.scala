package org.scalaquant.core.common.time.calendars

import java.time.LocalDate

object NullCalendar {

  private val impl =  new BusinessCalendar {

    override protected def considerBusinessDayShadow(implicit date: LocalDate): Boolean = true

    val name: String = "Null"

    override def isWeekend(date: LocalDate): Boolean = false
  }

  def apply(): BusinessCalendar = impl
}
