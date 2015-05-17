package org.scalaquant.core.indexes.ibor

import org.joda.time.LocalDate
import org.scalaquant.core.common.time.BusinessDayConvention.Following
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.common.time.{BusinessDayConvention, Period}
import org.scalaquant.core.currencies.Currency
import org.scalaquant.core.indexes.InterestRateIndex
import org.scalaquant.core.termstructures.YieldTermStructure

class IborIndex(val familyName: String,
                tenor: Period,
                settlementDays: Int,
                currency: Currency ,
                val fixingCalendar: BusinessCalendar ,
                val convention: BusinessDayConvention ,
                endOfMonth: Boolean,
                val dayCounter: DayCountConvention,
                val forwardingTermStructure: YieldTermStructure)
          extends InterestRateIndex(familyName,
                                    tenor,
                                    settlementDays,
                                    currency,
                                    fixingCalendar,
                                    dayCounter) {

  private def forecastFixing(valueDate: LocalDate, endDate: LocalDate, atTime: Double): Double ={
    require(forwardingTermStructure.nonEmpty)
    val disc1 = forwardingTermStructure.discount(valueDate)
    val disc2 = forwardingTermStructure.discount(endDate)
    (disc1/disc2 - 1.0) / atTime
  }

  override def forecastFixing(fixingDate: LocalDate): Double = {
    val d1 = valueDate(fixingDate)
    val d2 = maturityDate(d1)
    val t = dayCounter.fractionOfYear(d1, d2, d2)
    require(t > 0.0,
      s"cannot calculate forward rate between $d1 and $d2 non positive time ($t) using  ${dayCounter.name} daycounter")
     forecastFixing(d1, d2, t)
  }

  override def maturityDate(valueDate: LocalDate): LocalDate ={
    fixingCalendar.advance(valueDate, tenor.length, tenor.units, convention, endOfMonth)
  }
}


case class OvernightIndex(override val familyName: String,
                          settlementDays: Int,
                          currency: Currency ,
                          override val fixingCalendar: BusinessCalendar,
                          override val dayCounter: DayCountConvention,
                          h: YieldTermStructure) extends IborIndex(familyName,
                                                                    Period(1),
                                                                    settlementDays,
                                                                    currency,
                                                                    fixingCalendar,
                                                                    Following,
                                                                    false, dayCounter, h)