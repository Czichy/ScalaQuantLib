package org.scalaquant.core.indexes.swap

import org.scalaquant.common.time.Period
import org.scalaquant.common.time.calendars.BusinessCalendar
import org.scalaquant.common.time.daycounts.DayCountConvention
import org.scalaquant.core.currencies.Currency
import org.scalaquant.core.indexes.InterestRateIndex
import org.scalaquant.core.indexes.ibor.IborIndex
import org.scalaquant.core.termstructures.YieldTermStructure


class SwapIndex(familyName: String,
                tenor: Period,
                settlementDays: Int,
                currency: Currency,
                fixingCalendar: BusinessCalendar,
                fixingLegTenor: Period,
                fixingLegDayCounter: DayCountConvention,
                iborIndex: IborIndex)
        extends InterestRateIndex(familyName,
                                  tenor,
                                  settlementDays,
                                  currency,
                                  fixingCalendar,
                                  fixingLegDayCounter){

  def this(familyName: String,
           tenor: Period,
           settlementDays: Int,
           currency: Currency,
           fixingCalendar: BusinessCalendar,
           fixingLegTenor: Period,
           fixingLegDayCounter: DayCountConvention,
           iborIndex: IborIndex,
           discountingTermStructure: YieldTermStructure) = {}
}
