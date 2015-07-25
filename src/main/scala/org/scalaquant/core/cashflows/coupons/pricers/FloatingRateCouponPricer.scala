package org.scalaquant.core.cashflows.coupons.pricers

import org.scalaquant.core.cashflows.coupons.{FloatingRateCoupon, Coupon}

/**
 * Created by neo on 2015-07-13.
 */
object FloatingRateCouponPricer {

  def initialize(coupon: FloatingRateCoupon): FloatingRateCoupon = {
    coupon match {
      case IborCoupon =>
      case CmsCoupon =>
      case CmsSpreadCoupon =>
      case CappedFlooredIborCoupon =>
      case CappedFlooredCmsCoupon =>
      case CappedFlooredCmsSpreadCoupon =>
      case DigitalIborCoupon =>
      case DigitalCmsCoupon =>
      case DigitalCmsSpreadCoupon =>
      case RangeAccrualFloatersCoupon =>
      case SubPeriodsCoupon =>
    }
  }

}
