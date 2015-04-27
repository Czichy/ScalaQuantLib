package org.scalaquant.core.indexes

import org.joda.time.LocalDate
import org.scalaquant.core.common.Settings
import org.scalaquant.core.common.time.{TimeUnit, Period}
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.currencies.Currency
import org.scalaquant.core.common.time.JodaDateTimeHelper._
import rx.lang.scala.Observer


abstract class InterestRateIndex(familyName: String,
                        tenor: Period,
                        fixingDays: Int,
                        currency: Currency,
                        fixingCalendar: BusinessCalendar,
                        dayCounter: DayCountConvention) extends{

  def isValidFixingDate(fixingDate: LocalDate): Boolean = fixingCalendar.considerBusinessDay(fixingDate)

  def fixing(fixingDate: LocalDate, forecastTodaysFixing: Boolean = false): Double = {
    require(isValidFixingDate(fixingDate), "Fixing date " + fixingDate + " is not valid")

    val today = Settings.evaluationDate

    if (fixingDate > today || (fixingDate==today && forecastTodaysFixing)) {
       forecastFixing(fixingDate)
    } else if (fixingDate < today || Settings.enforcesTodaysHistoricFixings ) {
       pastFixing(fixingDate)
    } else {
       pastFixing(fixingDate).getOrElse(forecastFixing(fixingDate))
    }
  }

  def fixingDate(valueDate: LocalDate): LocalDate = {
    fixingCalendar.advance(valueDate, -fixingDays, TimeUnit.Days)
  }

  def valueDate(fixingDate: LocalDate): LocalDate = {
    require(isValidFixingDate(fixingDate), fixingDate + "is not a valid fixing date.")
    fixingCalendar.advance(fixingDate, fixingDays, TimeUnit.Days)
  }

  def maturityDate(fixingDate: LocalDate): LocalDate

  def pastFixing(fixingDate: LocalDate): Option[Double] = {
    require(isValidFixingDate(fixingDate), fixingDate + "is not a valid fixing date.")
    timeSeries.find(fixingDate)
  }

  def forecastFixing(fixingDate: LocalDate): Double

  def name: String = {
    val days = {
      val normalizedTenor = tenor.normalize
      if (normalizedTenor.units == TimeUnit.Days) {
        fixingDays match{
          case 0 => "ON"
          case 1 => "TN"
          case 2 => "SN"
          case _ => normalizedTenor.shortDescription
        }
      } else {
        normalizedTenor.shortDescription
      }
    }

    familyName + " " + days + " "+ dayCounter.name
  }
}
