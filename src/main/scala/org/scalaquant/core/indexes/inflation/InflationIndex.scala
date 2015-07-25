package org.scalaquant.core.indexes.inflation

import org.scalaquant.common.time.calendars.{ NullCalendar, BusinessCalendar }
import org.scalaquant.common.time.{Period, Frequency}
import org.scalaquant.core.currencies.Currency
import org.scalaquant.core.indexes.Region
import org.scalaquant.core.indexes.Index
import org.joda.time.LocalDate

trait InflationIndex extends Index{

  def familyName: String
  def region: Region
  def revised: Boolean
  def interpolated: Boolean
  def frequency: Frequency
  def availabilitiyLag: Period
  def currency: Currency

  override def isValidFixingDate(date: LocalDate):Boolean = true

  override val name = region.name + " " + familyName

  override def fixingCalendar: BusinessCalendar = NullCalendar.apply()

 }


class ZeroInflationIndex(val familyName: String,
                         val region: Region,
                         val revised: Boolean,
                         val interpolated: Boolean,
                         val frequency: Frequency,
                         val availabilityLag: Period,
                         val currency: Currency,
                         ts: ZeroInflationTermStructure) extends InflationIndex {

}

class YoYInflationIndex