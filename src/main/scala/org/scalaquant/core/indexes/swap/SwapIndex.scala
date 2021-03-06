package org.scalaquant.core.indexes.swap

import org.scalaquant.core.common.time.Period
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.currencies.Currency
import org.scalaquant.core.indexes.InterestRateIndex
import org.scalaquant.core.indexes.ibor.IBORIndex
import org.scalaquant.core.termstructures.YieldTermStructure


class SwapIndex(familyName: String,
                tenor: Period,
                settlementDays: Int,
                currency: Currency,
                fixingCalendar: BusinessCalendar,
                fixingLegTenor: Period,
                fixingLegDayCounter: DayCountConvention,
                iborIndex: IBORIndex,
                discountingTermStructure: YieldTermStructure)
        extends InterestRateIndex(familyName,
                                  tenor,
                                  settlementDays,
                                  currency,
                                  fixingCalendar,
                                  fixingLegDayCounter){


}
