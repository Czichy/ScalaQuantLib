package org.scalaquant.core.common.time.calendars

import java.time.LocalDate
import org.scalaquant.core.common.time.calendars.BusinessCalendar.{GeneralHolidays, Market}

/**
  * Created by neo on 12/13/15.
  */
object UnitedStates {
  case object NYSE extends Market
  case object Settlement extends Market
  case object GovernmentBond extends Market
  case object NERC extends Market

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

  //TODO: All the Impl here are wrong. copy/pasted from CANADA
  private val NYSEImpl = new BusinessCalendar with WeekEndSatSun{
    val name = "NYSE"
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

  private val NERCImpl = new BusinessCalendar with WeekEndSatSun{
    val name = "NERC"
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

  private val governmentBondImpl = new BusinessCalendar with WeekEndSatSun{
    val name = "GovernmentBond"
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
  private val settlementImpl = new BusinessCalendar with WeekEndSatSun{
    val name = "UnitedStates"
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
      case NYSE => NYSEImpl
      case NERC => NERCImpl
      case GovernmentBond => governmentBondImpl
      case Settlement => settlementImpl
      case _ => settlementImpl
    }
  }
}
