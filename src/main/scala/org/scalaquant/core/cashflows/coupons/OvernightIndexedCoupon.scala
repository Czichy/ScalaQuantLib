package org.scalaquant.core.cashflows.coupons

import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.common.time.Frequency

import org.joda.time.LocalDate
import org.scalaquant.core.cashflows.coupons.pricers.FloatingRateCouponPricer
import org.scalaquant.core.indexes.ibor.OvernightIndex

class OvernightIndexedCoupon(override val paymentDate: LocalDate,
                             override val nominal: Double,
                             override val startDate: LocalDate,
                             override val endDate: LocalDate,
                             override val fixingDays: Int,
                             override val index: OvernightIndex,
                             override val gearing: Double = 1.0,
                             override val spread: Double = 0.0,
                             override val freq: Frequency,
                             override val dayCounter: DayCountConvention,
                             override val pricer: FloatingRateCouponPricer,
                             override val isInArrears: Boolean = false)
  extends FloatingRateCoupon(paymentDate,
                              nominal,
                              startDate,
                              endDate,
                              fixingDays,
                                index,
                              gearing,
                              spread,
                              freq,
                              dayCounter,
                              pricer,
                              isInArrears
){



}
