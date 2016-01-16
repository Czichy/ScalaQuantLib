package org.scalaquant.core.common.time.calendars

import java.time.LocalDate
import org.scalaquant.core.common.time.calendars.BusinessCalendar._


object Canada{


  case object TSX extends Market
  case object Settlement extends Market

  import GeneralHolidays._

  private  def isFamilyDay(implicit date: LocalDate) = {
    val dom = date.getDayOfMonth
    (((dom >= 15 && dom <= 21) && isMonday) && date.getYear >= 2008) && inFebruary
  }
  private  def isVictoriaDay(implicit date: LocalDate) = {
    val dom = date.getDayOfMonth
    ((dom >= 17 && dom <= 24) && isMonday) && inMay
  }
  private  def isCanadaDay(implicit date: LocalDate) = {
    val dom = date.getDayOfMonth
    (dom == 1 || ((dom == 2 || dom == 3) && isMonday)) && inJuly
  }
  private  def isNovember11st(implicit date: LocalDate) = {
    val dom = date.getDayOfMonth
    (dom == 11 || ((dom == 12 || dom == 13) && isMonday)) && inNovember
  }


  private val TSXImpl = new BusinessCalendar with WeekEndSatSun{
    val name = "TSX"
    override protected def considerBusinessDayShadow(implicit date: LocalDate): Boolean = {
      !(isWeekend(date) || isGoodFriday || isNewYear || isNewYearOnMonday
                        || isFamilyDay
                        || isVictoriaDay
                        || isCanadaDay
                        || (isFirstMonday && inAugust)
                        || (isFirstMonday && inSeptember)
                        || (isSecondMonday && inOctober)
                        || isChristmas
                        || isBoxingDay)
    }
  }

  private val SettlementImpl = new BusinessCalendar with WeekEndSatSun{
    val name = "Canada"
    override protected def considerBusinessDayShadow(implicit date: LocalDate): Boolean = {
      !(isWeekend(date) || isGoodFriday || isNewYear || isNewYearOnMonday
                        || isFamilyDay
                        || isVictoriaDay
                        || isCanadaDay
                        || (isFirstMonday && inAugust)
                        || (isFirstMonday && inSeptember)
                        || (isSecondMonday && inOctober)
                        || isNovember11st
                        || isChristmas
                        || isBoxingDay)

    }
  }
  def apply(market: Market = Settlement): BusinessCalendar = {
    market match {
      case TSX => TSXImpl
      case Settlement => SettlementImpl
      case _ => SettlementImpl
    }
  }
}