package org.scalaquant.core.termstructures.yields

import org.joda.time.LocalDate
import org.scalaquant.common.Compounding._
import org.scalaquant.common.InterestRate
import org.scalaquant.common.time.Frequency._
import org.scalaquant.common.time.JodaDateTimeHelper
import org.scalaquant.common.time.calendars.BusinessCalendar
import org.scalaquant.common.time.daycounts.DayCountConvention
import org.scalaquant.core.quotes.ValidQuote
import org.scalaquant.core.termstructures.YieldTermStructure
import org.scalaquant.core.types.{DiscountFactor, YearFraction}

case class FlatForward(override val settlementDays: Int,
                       override val referenceDate: LocalDate,
                       override val calendar: BusinessCalendar,
                       forward: ValidQuote,
                       dayCounter: DayCountConvention,
                       compounding: Compounding = Continuous,
                       frequency: Frequency = Annual,
                       allowsExtrapolation: Boolean = false)
  extends YieldTermStructure(settlementDays, referenceDate, calendar, dayCounter) {

    def maxDae = JodaDateTimeHelper.farFuture

    def discountImpl(time: YearFraction): DiscountFactor = rate.discountFactor(time)

    def maxDate: LocalDate = JodaDateTimeHelper.farFuture

    private def rate = InterestRate(forward.value, dayCounter, compounding, frequency)

}