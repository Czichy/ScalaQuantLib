package org.scalaquant.core.cashflows.coupons

import org.scalaquant.core.common.time.Period
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.indexes.inflation.YoYInflationIndex

import org.scalaquant.core.types._
import java.time.LocalDate

class YoYInflationCoupon( paymentDate: LocalDate, //the upcoming payment date of this coupon
                          nominal: Rate,
                          accrualStartDate: LocalDate, //usually the payment date of last coupon
                          accrualEndDate: LocalDate, //usually the sttlement date of the coupon
                          refPeriodStart: Option[LocalDate],
                          refPeriodEnd: Option[LocalDate],
                          exCouponDate: Option[LocalDate],
                          fixingDays: Natural,
                          index: YoYInflationIndex,
                          observationLag: Period,
                          dayCounter: DayCountConvention,
                          gearing: Double = 1.0,
                          spread: Spread = 0.0) {


}
