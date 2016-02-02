package org.scalaquant.core.indexes.ibor

import org.scalaquant.core.common.time.calendars.BusinessCalendar

/**
  * Created by Neo Lin on 2016-01-27.
  */
trait Libor extends IBORIndex{
  def financialCenterCalendar: BusinessCalendar
  def jointCalendar: BusinessCalendar
}
