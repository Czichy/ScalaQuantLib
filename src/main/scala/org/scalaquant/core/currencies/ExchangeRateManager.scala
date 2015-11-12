package org.scalaquant.core.currencies

import org.joda.time.{DateTime, LocalTime, LocalDate}
import org.scalaquant.core.common.Settings
import org.scalaquant.core.common.time.JodaDateTimeHelper
import org.scalaquant.core.currencies.America.{ PEHCurrency, PENCurrency, PEICurrency }
import org.scalaquant.core.currencies.Europe._

import scala.collection.concurrent.TrieMap

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

import JodaDateTimeHelper._

trait ExchangeRateManager {

  def add(rate: ExchangeRate, startDate: LocalDate, endDate: LocalDate): Unit //Daily Rate

  def lookup(source: Currency, target: Currency,
             date: LocalDate = Settings.evaluationDate,
             exchangeType: ExchangeRate.ExchangeType = ExchangeRate.Derived()): ExchangeRate

  def clear(): Unit
}

trait DailyRates {
  def add(rate: ExchangeRate, startTime: LocalTime, endTime: LocalTime): Unit //Hourly Rate
  def add(rate: ExchangeRate, startTime: DateTime, endTime: DateTime): Unit //Hourly Rate with Dates
  case class HourlyEntry(rate: ExchangeRate, startTime: LocalTime, endTime: LocalTime) {
    def isValidAt(date: LocalTime): Boolean = {
      (date >= startTime) && (date <= endTime)
    }
  }

//  case class HourlyEntryWithDate(rate: ExchangeRate, startTime: DateTime, endTime: DateTime) {
//    def isValidAt(date: DateTime): Boolean = {
//      (date >= startTime) && (date <= endTime)
//    }
//  }
}

object ExchangeRateManager {
  import org.joda.time.DateTimeConstants._
  private val knownRates = TrieMap(
    Key(EUR, ATS) -> Entry(ExchangeRate(EUR, ATS, 13.7603), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EUR, BEF) -> Entry(ExchangeRate(EUR, BEF, 40.3399), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EUR, DEM) -> Entry(ExchangeRate(EUR, DEM, 1.95583), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EUR, ESP) -> Entry(ExchangeRate(EUR, ESP, 166.386), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EUR, FIM) -> Entry(ExchangeRate(EUR, FIM, 5.94573), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EUR, FRF) -> Entry(ExchangeRate(EUR, FRF, 6.55957), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EUR, GRD) -> Entry(ExchangeRate(EUR, GRD, 340.750), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EUR, IEP) -> Entry(ExchangeRate(EUR, IEP, 0.787564), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EUR, ITL) -> Entry(ExchangeRate(EUR, ITL, 1936.27), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EUR, LUF) -> Entry(ExchangeRate(EUR, LUF, 40.3399), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EUR, NLG) -> Entry(ExchangeRate(EUR, NLG, 2.20371), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EUR, PTE) -> Entry(ExchangeRate(EUR, PTE, 200.482), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(TRY, TRL) -> Entry(ExchangeRate(TRY, TRL, 1000000.0), new LocalDate(2005, JANUARY, 1), farFuture),
    Key(RON, ROL) -> Entry(ExchangeRate(RON, ROL, 10000.0), new LocalDate(2005, JULY, 1), farFuture)
   // Key(PEN, PEI) -> Entry(ExchangeRate(PEN, PEI, 1000000.0), new LocalDate(1991, JULY, 1), farFuture),
   // Key(PEI, PEH) -> Entry(ExchangeRate(PEI, PEH, 1000.0), new LocalDate(1985, FEBRUARY, 1), farFuture)
  )
  case class Entry(rate: ExchangeRate, startDate: LocalDate, endDate: LocalDate) {
    def isValidAt(date: LocalDate): Boolean = {
      (date >= startDate) && (date <= endDate)
    }
  }

  case class Key(c1: Currency, c2: Currency)
}

object DefaultExchangeRateManager extends ExchangeRateManager {
  import org.scalaquant.core.currencies.ExchangeRateManager.{ Key, Entry }
  private var entries = Map.empty[Key, Entry]

  def clear(): Unit = {
    entries = Map.empty[Key, Entry]
  }

  def add(rate: ExchangeRate, startDate: LocalDate, endDate: LocalDate): Unit = {
    entries = entries + (Key(rate.source, rate.target) -> Entry(rate, startDate, endDate))
  }

  private def directLookup(source: Currency, target: Currency, date: LocalDate): ExchangeRate = {
    entries.get(Key(source, target)).filter(_.isValidAt(date)).map(_.rate).getOrElse(ExchangeRate.Unknown)
  }

  private def smartLookUp(source: Currency, target: Currency, date: LocalDate): ExchangeRate = {
     directLookup(source, target, date) match {
       case ExchangeRate.Unknown =>
           val left = entries.keys.filter(key => (key.c1 == source && key.c2 != target) || (key.c2 == source && key.c1 != target))
           val right = entries.keys.filter(key => (key.c1 == target && key.c2 != source) || (key.c2 == target && key.c1 != source))

            (left zip right).filter{ case (key1, key2) =>
              if (key1.c1 == source) key1.c2 == key2.c1 || key1.c2 == key2.c2 else key1.c1 == key2.c1 || key1.c1 == key2.c2
            }.collectFirst {
              case (key1, key2) => ExchangeRate.chain(entries(key1).rate, entries(key2).rate)
            }.getOrElse {
              ExchangeRate.UnChainable
            }
       case attempt => attempt
    }

  }

  override def lookup(source: Currency, target: Currency,
    date: LocalDate = Settings.evaluationDate,
    exchangeType: ExchangeRate.ExchangeType = ExchangeRate.Derived()): ExchangeRate = {
    source == target match {
      case true  => ExchangeRate(source, target, 1.0)
      case false =>
        exchangeType match {
          case ExchangeRate.Direct => directLookup(source, target, date)
          case ExchangeRate.Derived(_, _) =>
            (source.triangulationCurrency, target.triangulationCurrency) match {
              case (Some(link), None) =>
                if (link == target) {
                  directLookup(source, link, date)
                } else {
                  ExchangeRate.chain(directLookup(source, link, date), lookup(link, target, date))
                }
              case (None, Some(link)) =>
                if (link == source) {
                  directLookup(link, target, date)
                } else {
                  ExchangeRate.chain(lookup(source, link, date), directLookup(link, target, date))
                }
              case _ => smartLookUp(source, target, date)
            }
        }

    }

  }
}
