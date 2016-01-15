package org.scalaquant.core.termstructures


import org.joda.time.{ Days, LocalDate }
import org.scalaquant.core.common.time.Frequency._
import org.scalaquant.core.common.time.JodaDateTimeHelper._

import org.scalaquant.core.indexes.inflation.InflationIndex.inflationPeriod
import org.scalaquant.core.common.time.{TimeUnit, Period}
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.termstructures.inflation.Seasonality
import org.scalaquant.core.types.{YearFraction, Rate}

trait ZeroInflationTermStructure{
    self : InflationTermStructure =>

    protected def zeroRateImpl(time: YearFraction): Rate

    def zeroRate(asOf: LocalDate, instObsLag: Period,
                forceLinearInterpolation: Boolean,
                extrapolate: Boolean = false) = {

        val useLag = if (instObsLag == Period(-1, TimeUnit.Days)) observationLag else instObsLag

        val (startDate, endDate) = inflationPeriod(asOf - useLag, frequency)

        val zeroRate = if (forceLinearInterpolation) {

            val dp = Days.daysBetween(endDate + Period(1, TimeUnit.Days), startDate).getDays
            val dt = Days.daysBetween(asOf , startDate).getDays
            // if we are interpolating we only check the exact point
            // this prevents falling off the end at curve maturity
            checkRange(asOf, extrapolate)
            val z1 = zeroRateImpl(timeFromReference(startDate))
            val z2 = zeroRateImpl(timeFromReference(endDate))

             z1 + (z2 - z1) * (dt/dp)
        } else {
            if (indexIsInterpolated) {
                checkRange(asOf - useLag, extrapolate)
                zeroRateImpl(timeFromReference(asOf - useLag))
            } else {
                checkRange(startDate, extrapolate)
                zeroRateImpl(timeFromReference(startDate))
            }
        }

        if (seasonality.isConsistent(this)) {
            seasonality.correctZeroRate(asOf - useLag, zeroRate, this)
        } else {
            zeroRate
        }
    }

}
