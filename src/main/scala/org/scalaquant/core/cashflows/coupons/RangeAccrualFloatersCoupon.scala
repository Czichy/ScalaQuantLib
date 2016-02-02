package org.scalaquant.core.cashflows.coupons

import java.time.LocalDate

import org.scalaquant.core.common.time.{PaymentSchedule, Period}
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.indexes.ibor.IBORIndex
import org.scalaquant.core.types._

/**
  * Created by Neo Lin on 2016-01-17.
  */
class RangeAccrualFloatersCoupon(paymentDate: LocalDate, //the upcoming payment date of this coupon
                                 nominal: Rate,
                                 accrualStartDate: LocalDate, //usually the payment date of last coupon
                                 accrualEndDate: LocalDate, //usually the sttlement date of the coupon
                                 refPeriodStart: Option[LocalDate],
                                 refPeriodEnd: Option[LocalDate],
                                 exCouponDate: Option[LocalDate],
                                 fixingDays: Natural,
                                 index: IBORIndex,
                                 observationsSchedule: PaymentSchedule,
                                 lowerTrigger: Double, // l
                                 upperTrigger: Double,
                                 dayCounter: DayCountConvention,
                                 gearing: Double = 1.0,
                                 spread: Spread = 0.0){

}
