package org.scalaquant.common.time.calendars

import org.joda.time.LocalDate
import org.scalaquant.common.time.calendars.BusinessCalendar._

object Argentina {

  case object Merval extends Market

  import GeneralHolidays._

  private def isMayRevolutionDay(implicit date: LocalDate) = {
    date.getDayOfMonth == 1 && inMay
  }
  private def isLabourDay(implicit date: LocalDate) = {
    date.getDayOfMonth == 25 && inMay
  }
  private def isHolyThursday(implicit date: LocalDate) = {
    date.getDayOfYear == Western.easterMonday(date.getYear) - 4
  }
  private def theThirdWeek(implicit date: LocalDate) = {
    val dom = date.getDayOfMonth
    dom >= 15 && dom <= 21 && isMonday
  }
  private def isDeathofGeneralManuelBelgrano(implicit date: LocalDate) = {
    theThirdWeek && inJune
  }
  private def isIndependenceDay(implicit date: LocalDate) = {
    date.getDayOfMonth == 9 && inJuly
  }
  private def isDeathofGeneralJosédeSanMartín(implicit date: LocalDate) = {
    theThirdWeek && inAugust
  }
  private def isColumbusDay(implicit date: LocalDate) = {
    val dom = date.getDayOfMonth
    ((dom == 10 || dom == 11 || dom == 12 || dom == 15 || dom == 16) && isMonday) && inOctober
  }
  private def isImmaculateConception(implicit date: LocalDate) = {
    date.getDayOfMonth == 8 && inDecember
  }
  private def isChristmasEve(implicit date: LocalDate) = {
    date.getDayOfMonth == 24 && inDecember
  }
  private def isNewYearEve(implicit date: LocalDate) = {
    val dom = date.getDayOfMonth
    (dom == 31 || (dom == 30 && isFriday)) && inDecember
  }

  private val MervalImpl = new BusinessCalendar with WeekEndSatSun {
    def name: String = "Buenos Aires stock exchange"
    protected def considerBusinessDayShadow(implicit date: LocalDate): Boolean = {
      !(isWeekend(date) || isHolyThursday || isGoodFriday
                        || isNewYear
                        || isMayRevolutionDay
                        || isLabourDay
                        || isDeathofGeneralManuelBelgrano
                        || isIndependenceDay
                        || isDeathofGeneralJosédeSanMartín
                        || isColumbusDay
                        || isImmaculateConception || isChristmasEve || isNewYearEve)

    }
  }

  def apply(market: Market) = market match {
    case Merval => MervalImpl
    case _ => MervalImpl
  }

}
