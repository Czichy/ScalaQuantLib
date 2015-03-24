package org.scalaquant.core.indexes.ibor

import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.common.time.{BusinessDayConvention, Period}
import org.scalaquant.core.currencies.Currency
import org.scalaquant.core.indexes.InterestRateIndex
import org.scalaquant.core.termstructures.YieldTermStructure

class IborIndex(familyName: String,
                tenor: Period,
                settlementDays: Int,
                currency: Currency ,
                fixingCalendar: BusinessCalendar ,
                convention: BusinessDayConvention ,
                endOfMonth: Boolean,
                dayCounter: DayCountConvention,
                forwardingTermStructure: YieldTermStructure)
          extends InterestRateIndex(familyName,
                                    tenor,
                                    settlementDays,
                                    currency,
                                    fixingCalendar,
                                    dayCounter) {

}
