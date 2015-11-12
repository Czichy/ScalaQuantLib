package org.scalaquant.core.cashflows

import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.cashflows.coupons.Coupon
import org.scalaquant.core.cashflows.coupons.pricers.Pricer
import org.scalaquant.core.types.{YearFraction, Rate}

/**
 * Created by neo on 2015-07-19.
 */
package object coupons {
  type DayCounts[T] = (Coupon, DayCountConvention) => T
  val accrualDays: DayCounts[Int] = (coupon, dayCounter) =>
      dayCounter.dayCount(coupon.accrualStartDate, coupon.accrualEndDate)

  //type
//  val accrualPeriod: DayCounts[YearFraction] = (coupon, dayCounter) =>
//    case coupon:
//    dayCounter.fractionOfYear(coupon.accrualStartDate, coupon.accrualEndDate, coupon.paymentDate)

  //  type YearFractionCount = accrualDays
//  type CouponPricing = (Coupon, Pricer, DayCountConvention) => Rate
//  val pricing: CouponPricing = (coupon, pricer, dayCounter) => {
//
//  }
//  type CouponPricing = (Coupon, Pricer, DayCountConvention) => Rate
//  val pricing: CouponPricing = (coupon, pricer, dayCounter) => {
//
//  }

}
