package org.scalaquant.core.common.time.calendars

import org.joda.time.LocalDate
import org.scalaquant.core.common.time.calendars.BusinessCalendar._

object Brazil {
  case object Settlement extends Market
  case object Exchange extends Market
  import GeneralHolidays._
  private def isTiradentesDay(implicit date: LocalDate): Boolean = {
    date.getDayOfMonth == 21 && inApril
  }
  private def isLaborDay(implicit date: LocalDate): Boolean = {
    date.getDayOfMonth == 1 && inMay
  }
  private def isRevolutionDay(implicit date: LocalDate): Boolean = {
    date.getDayOfMonth == 9 && inJuly
  }
  private def isIndependenceDay(implicit date: LocalDate): Boolean = {
    date.getDayOfMonth == 7 && inSeptember
  }
  private def isNossaSraAparecidaDay(implicit date: LocalDate): Boolean = {
    date.getDayOfMonth == 12 && inOctober
  }
  private def isAllSoulsDay(implicit date: LocalDate): Boolean = {
    date.getDayOfMonth == 2 && inNovember
  }
  private def isRepublicDay(implicit date: LocalDate): Boolean = {
    date.getDayOfMonth == 15 && inNovember
  }
  private def isBlackConsciousnessDay(implicit date: LocalDate): Boolean = {
    date.getDayOfMonth == 20 && inNovember && date.getYear >= 2007
  }
  private def isChristmasBrazil(implicit date: LocalDate): Boolean = {
    date.getDayOfMonth == 25 && inDecember
  }
  private def isSaoPauloCityDay(implicit date: LocalDate): Boolean = {
    date.getDayOfMonth == 25 && inJanuary
  }
  private def isLastBusinessDay(implicit date: LocalDate): Boolean = {
    val dom = date.getDayOfMonth
    inDecember && (dom == 31 || (dom >= 29 && isFriday))
  }
  import GeneralHolidays.{isGoodFriday => isPassionofChrist}

  private def isCarnival(implicit date: LocalDate): Boolean = {
    val em = Western.easterMonday(date.getYear)
    val doy = date.getDayOfYear
    (doy == em -  49) || (doy == em - 48)
  }

  private def isCorpusChristi(implicit date: LocalDate): Boolean = {
    val em = Western.easterMonday(date.getYear)
    val doy = date.getDayOfYear
    doy == em + 59
  }
  private val settlementImpl = new BusinessCalendar with WeekEndSatSun {

    override protected def considerBusinessDayShadow(implicit date: LocalDate): Boolean = {
      !(isWeekend(date) || isNewYear || isTiradentesDay
                        || isLaborDay || isIndependenceDay
                        || isNossaSraAparecidaDay || isAllSoulsDay
                        || isRepublicDay || isChristmasBrazil || isPassionofChrist
                        || isCarnival || isCorpusChristi)
    }

    override def name: String = "Brazil"
  }

  private val exchangeImpl = new BusinessCalendar with WeekEndSatSun {

    override protected def considerBusinessDayShadow(implicit date: LocalDate): Boolean = {
      !(isWeekend(date) || isNewYear || isSaoPauloCityDay
        || isTiradentesDay
        || isLaborDay || isIndependenceDay
        || isRevolutionDay
        || isNossaSraAparecidaDay || isAllSoulsDay || isBlackConsciousnessDay
        || isRepublicDay || isChristmasBrazil || isPassionofChrist
        || isCarnival || isCorpusChristi || isLastBusinessDay)
    }

    override def name: String = "BOVESPA"
  }

  def apply(market: Market = Settlement): BusinessCalendar = {
    market match {
      case Settlement => settlementImpl
      case Exchange => exchangeImpl
      case _ => settlementImpl
    }
  }
}
