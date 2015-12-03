package org.scalaquant.core.indexes.inflation


import org.scalaquant.core.common.Settings
import org.scalaquant.core.common.time.Frequency.Frequency
import org.scalaquant.core.common.time.{TimeUnit, Period}
import org.scalaquant.core.currencies.Currency
import org.scalaquant.core.indexes.Region
import org.scalaquant.core.indexes.Index
import org.joda.time.{Days, DateTimeConstants, LocalDate}
import org.scalaquant.core.common.time.JodaDateTimeHelper._
import org.scalaquant.core.termstructures.ZeroInflationTermStructure

abstract class InflationIndex(val familyName: String,
                              val region: Region,
                              val revised: Boolean,
                              val interpolated: Boolean,
                              val frequency: Frequency,
                              val availabilityLag: Period,
                              val currency: Currency) extends Index{


  def isValidFixingDate(date: LocalDate):Boolean = true

  def name = region.name + " " + familyName

  //def fixingCalendar: BusinessCalendar = NullCalendar.apply()

 }

object InflationIndex{

  import org.scalaquant.core.common.time.Frequency._
  import DateTimeConstants._

  def inflationPeriod(asOf: LocalDate, frequency: Frequency) = {

    val year = asOf.getYear
    def month = asOf.getDayOfMonth

    frequency match {
      case Annual =>
        (firstDayOf(JANUARY, year), lastDayOf(DECEMBER, year))

      case Semiannual =>
        val start = 6*((month-1)/6) + 1
        (firstDayOf(start, year), lastDayOf(start + 5, year))

      case Quarterly =>
        val start = 3*((month-1)/3) + 1
        (firstDayOf(start, year), lastDayOf(start + 2, year))

      case Monthly =>
        (firstDayOf(month, year), lastDayOf(month,year))

      case _ => (LocalDate.now(), LocalDate.now)

    }
  }
}

abstract class ZeroInflationIndex(familyName: String,
                         region: Region,
                         revised: Boolean,
                         interpolated: Boolean,
                         frequency: Frequency,
                         availabilityLag: Period,
                         currency: Currency,
                         val termStructure: ZeroInflationTermStructure)
  extends InflationIndex(familyName,
                         region,
                         revised,
                         interpolated,
                         frequency,
                         availabilityLag,
                         currency) {

  import InflationIndex._
  import org.scalaquant.math.Comparing.Implicits._
  import org.scalaquant.math.Comparing.ImplicitsOps._


  private def needsForecast(fixingDate: LocalDate) = {

    val today = Settings.evaluationDate
    val todayMinusLag = today - availabilityLag

    val historicalFixingKnown = inflationPeriod(todayMinusLag, frequency)._1.plusDays(-1)

    val latestNeededDate = if (interpolated) { // might need the next one too
      val (start, _) = inflationPeriod(fixingDate, frequency)
      if (fixingDate > start) fixingDate + Period(frequency) else fixingDate
    }else{
      fixingDate
    }

    if (latestNeededDate <= historicalFixingKnown) {
      false
    } else if (latestNeededDate > today) {
      true
    } else {
       timeSeries.find(latestNeededDate).isEmpty
    }
  }

  def forecastFixing(fixingDate: LocalDate) = {
    // the term structure is relative to the fixing value at the base date.
    val baseDate = termStructure.baseDate
    require(!needsForecast(baseDate), name + " index fixing at base date is not available")

    val effectiveFixingDate = if (interpolated) fixingDate else inflationPeriod(fixingDate, frequency)._1

    // no observation lag because it is the fixing for the date
    // but if index is not interpolated then that fixing is constant
    // for each period, hence the t uses the effectiveFixingDate

    // However, it's slightly safe to get the zeroRate with the
    // fixingDate to avoid potential problems at the edges of periods
    val t = termStructure.dc.fractionOfYear(baseDate, effectiveFixingDate)
    val zero = termStructure.zeroRate(fixingDate, Period(0, TimeUnit.Days), forceLinearInterpolation = false)

    fixing(baseDate) * Math.pow(1.0 + zero, t)
  }

  def fixing(fixingDate: LocalDate, forecastTodaysFixing: Boolean = false): Double = {
    if (!needsForecast(fixingDate)) {
      val (startDate, endDate) = inflationPeriod(fixingDate, frequency)
      val pastFixing = timeSeries.find(startDate)

      (if (interpolated) {
        pastFixing.flatMap{
          fixing =>
            if (fixingDate == startDate) {
              Some(fixing)
            } else {
               timeSeries.find(endDate.plusDays(1)).map{ nextFixing =>
                 fixing + (nextFixing - fixing) * Days.daysBetween(fixingDate, startDate).getDays / Days.daysBetween(endDate.plusDays(1), startDate).getDays
               }
            }
        }
      } else {
        pastFixing
      }).getOrElse(Double.NaN)
    } else {

      forecastFixing(fixingDate);
    }
  }
}

//
//class YoYInflationIndex(familyName: String,
//                        region: Region,
//                        revised: Boolean,
//                        interpolated: Boolean,
//                        frequency: Frequency,
//                        availabilityLag: Period,
//                        currency: Currency,
//                        val ratio: Boolean,
//                        val termStructure: YoYInflationTermStructure)
//  extends InflationIndex( familyName,
//                          region,
//                          revised,
//                          interpolated,
//                          frequency,
//                          availabilityLag,
//                          currency) {
//
//}