package org.scalaquant.core.common.time.calendars

import java.time.LocalDate

object Australia {
  import BusinessCalendar.GeneralHolidays._

  private def isAustraliaDay(implicit date: LocalDate) = {
    val dom = date.getDayOfMonth
    (dom == 26 || ((dom == 27 || dom == 28) && isMonday)) && inJanuary
  }
  private def isANZACDay(implicit date: LocalDate) = {
    val dom = date.getDayOfMonth
    (dom == 25 || (dom == 26 && isMonday)) && inApril
  }

  private val Impl = new BusinessCalendar with WeekEndSatSun {
    def name: String = "Australia"
    protected def considerBusinessDayShadow(implicit date: LocalDate): Boolean = {
      !(isWeekend(date) || isEasterMonday || isGoodFriday
                        || isNewYear || isAustraliaDay
                        || isANZACDay
                        || (isSecondMonday && inJune)
                        || (isFirstMonday && inAugust)
                        || (isFirstMonday && inOctober)
                        || isChristmas
                        || isBoxingDay)
    }
  }
  def apply(): BusinessCalendar = Impl
}
