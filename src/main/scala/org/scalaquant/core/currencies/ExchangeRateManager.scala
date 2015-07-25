package org.scalaquant.core.currencies

import org.joda.time.{DateTime, LocalTime, LocalDate}
import org.scalaquant.common.Settings
import org.scalaquant.core.currencies.America.{ PEHCurrency, PENCurrency, PEICurrency }
import org.scalaquant.core.currencies.Europe._

import scala.collection.concurrent.TrieMap
import scala.language.implicitConversions
import org.scalaquant.common.time.JodaDateTimeHelper._

/**
 * Created by neo on 2015-03-03.
 */

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

  case class HourlyEntryWithDate(rate: ExchangeRate, startTime: DateTime, endTime: DateTime) {
    def isValidAt(date: DateTime): Boolean = {
      (date >= startTime) && (date <= endTime)
    }
  }
}

object ExchangeRateManager {
  import org.joda.time.DateTimeConstants._
  private val knownRates = TrieMap(
    Key(EURCurrency(), ATSCurrency()) -> Entry(ExchangeRate(EURCurrency(), ATSCurrency(), 13.7603), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EURCurrency(), BEFCurrency()) -> Entry(ExchangeRate(EURCurrency(), BEFCurrency(), 40.3399), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EURCurrency(), DEMCurrency()) -> Entry(ExchangeRate(EURCurrency(), DEMCurrency(), 1.95583), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EURCurrency(), ESPCurrency()) -> Entry(ExchangeRate(EURCurrency(), ESPCurrency(), 166.386), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EURCurrency(), FIMCurrency()) -> Entry(ExchangeRate(EURCurrency(), FIMCurrency(), 5.94573), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EURCurrency(), FRFCurrency()) -> Entry(ExchangeRate(EURCurrency(), FRFCurrency(), 6.55957), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EURCurrency(), GRDCurrency()) -> Entry(ExchangeRate(EURCurrency(), GRDCurrency(), 340.750), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EURCurrency(), IEPCurrency()) -> Entry(ExchangeRate(EURCurrency(), IEPCurrency(), 0.787564), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EURCurrency(), ITLCurrency()) -> Entry(ExchangeRate(EURCurrency(), ITLCurrency(), 1936.27), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EURCurrency(), LUFCurrency()) -> Entry(ExchangeRate(EURCurrency(), LUFCurrency(), 40.3399), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EURCurrency(), NLGCurrency()) -> Entry(ExchangeRate(EURCurrency(), NLGCurrency(), 2.20371), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(EURCurrency(), PTECurrency()) -> Entry(ExchangeRate(EURCurrency(), PTECurrency(), 200.482), new LocalDate(1999, JANUARY, 1), farFuture),
    Key(TRYCurrency(), TRLCurrency()) -> Entry(ExchangeRate(TRYCurrency(), TRLCurrency(), 1000000.0), new LocalDate(2005, JANUARY, 1), farFuture),
    Key(RONCurrency(), ROLCurrency()) -> Entry(ExchangeRate(RONCurrency(), ROLCurrency(), 10000.0), new LocalDate(2005, JULY, 1), farFuture),
    Key(PENCurrency(), PEICurrency()) -> Entry(ExchangeRate(PENCurrency(), PEICurrency(), 1000000.0), new LocalDate(1991, JULY, 1), farFuture),
    Key(PEICurrency(), PEHCurrency()) -> Entry(ExchangeRate(PEICurrency(), PEHCurrency(), 1000.0), new LocalDate(1985, FEBRUARY, 1), farFuture)
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
            (source.definition.triangulationCurrency, target.definition.triangulationCurrency) match {
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
//
        }

    }

  }
}
