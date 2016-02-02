package org.scalaquant.core.indexes.ibor

import java.time.LocalDate
import org.scalaquant.core.common.time.BusinessDayConvention.Following
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.common.time.{BusinessDayConvention, Period}
import org.scalaquant.core.currencies.Currency
import org.scalaquant.core.indexes.InterestRateIndex
import org.scalaquant.core.termstructures.YieldTermStructure
import org.scalaquant.core.types.Rate

trait IBORIndex extends InterestRateIndex {

  def convention: BusinessDayConvention

  def endOfMonth: Boolean

  def forwardingTermStructure: YieldTermStructure

  def forecastFixing(valueDate: LocalDate, endDate: LocalDate, atTime: Double): Option[Rate] ={
    require(forwardingTermStructure.nonEmpty)

    val disc1 = forwardingTermStructure.discount(valueDate)
    val disc2 = forwardingTermStructure.discount(endDate)

    Some((disc1/disc2 - 1.0) / atTime)
  }

  def forecastFixing(fixingDate: LocalDate): Option[Rate] = {
    val d1 = valueDate(fixingDate)
    val d2 = maturityDate(d1)
    val t = dayCounter.fractionOfYear(d1, d2)

    require(t > 0.0, s"cannot calculate forward rate between $d1 and $d2 non positive time ($t) using  ${dayCounter.name} daycounter")

    forecastFixing(d1, d2, t)
  }

  override def maturityDate(valueDate: LocalDate): LocalDate ={
    fixingCalendar.advance(valueDate, tenor.length, tenor.units, convention, endOfMonth)
  }
}


case class OvernightIndex(familyName: String,
                          tenor: Period = Period(1),
                          settlementDays: Int,
                          fixingDays: Int,
                          currency: Currency,
                          fixingCalendar: BusinessCalendar,
                          convention: BusinessDayConvention = Following,
                          endOfMonth: Boolean = false,
                          dayCounter: DayCountConvention,
                          forwardingTermStructure: YieldTermStructure) extends IBORIndex