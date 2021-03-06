package org.scalaquant.core.indexes

import java.time.LocalDate
import org.scalaquant.core.common.Settings
import org.scalaquant.core.common.time.{TimeUnit, Period}
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.currencies.Currency

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

trait InterestRateIndex extends Index{

  def familyName: String

  def tenor: Period

  def fixingDays: Int

  def currency: Currency

  def dayCounter: DayCountConvention

  private def requireFixingDate(value: LocalDate): Unit = {
    require(isValidFixingDate(value), value + "is not a valid fixing date.")
  }

  def isValidFixingDate(fixingDate: LocalDate): Boolean = fixingCalendar.considerBusinessDay(fixingDate)

  def fixing(fixingDate: LocalDate, forecastTodaysFixing: Boolean = false): Double = {
    requireFixingDate(fixingDate)

    val today = Settings.evaluationDate

    if (fixingDate > today || (fixingDate == today && forecastTodaysFixing) ) {
       forecastFixing(fixingDate)
    } else if (fixingDate < today || Settings.enforcesTodaysHistoricFixings ) {
       pastFixing(fixingDate).get
    } else {
       pastFixing(fixingDate).getOrElse(forecastFixing(fixingDate))
    }
  }

  def fixingDate(valueDate: LocalDate): LocalDate = fixingCalendar.advance(valueDate, -fixingDays, TimeUnit.Days)

  def valueDate(fixingDate: LocalDate): LocalDate = {
    requireFixingDate(fixingDate)
    
    fixingCalendar.advance(fixingDate, fixingDays, TimeUnit.Days)
  }

  def maturityDate(fixingDate: LocalDate): LocalDate

  def pastFixing(fixingDate: LocalDate): Option[Double] = {
    requireFixingDate(fixingDate)
    
    timeSeries.find(fixingDate)
  }

  def forecastFixing(fixingDate: LocalDate): Double

  def name: String = {
    val days = {
      val normalizedTenor = tenor.normalize
      if (normalizedTenor.units == TimeUnit.Days) {
        fixingDays match {
          case 0 => "ON"
          case 1 => "TN"
          case 2 => "SN"
          case _ => normalizedTenor.shortDescription
        }
      } else {
        normalizedTenor.shortDescription
      }
    }

    familyName + " " + days + " " + dayCounter.name
  }
}
