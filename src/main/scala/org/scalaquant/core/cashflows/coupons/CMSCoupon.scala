package org.scalaquant.core.cashflows.coupons

import java.time.LocalDate

import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.indexes.swap.SwapIndex
import org.scalaquant.core.types._

/**
  * Created by neo on 11/7/15.
  */
final case class CMSCoupon( paymentDate: LocalDate, //the upcoming payment date of this coupon
                            nominal: Rate,
                            accrualStartDate: LocalDate, //usually the payment date of last coupon
                            accrualEndDate: LocalDate, //usually the sttlement date of the coupon
                            refPeriodStart: Option[LocalDate],
                            refPeriodEnd: Option[LocalDate],
                            exCouponDate: Option[LocalDate],
                            fixingDays: Int,
                            index: SwapIndex,
                            gearing: Double = 1.0,
                            spread: Spread = 0.0,
                            dayCounter: DayCountConvention,
                            isInArrears: Boolean = false)
  extends FloatingRateCoupon