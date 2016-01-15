package org.scalaquant.core.cashflows.coupons.pricers

import org.scalaquant.core.cashflows.coupons.Coupon
import org.scalaquant.core.types._


sealed trait Pricer {

  def swapletPrice: Rate

  def swapletRate: Rate

  def capletPrice(effectiveCap: Rate): Rate

  def capletRate(effectiveCap: Rate): Rate

  def floorletPrice(effectiveFloor: Rate): Rate

  def floorletRate(effectiveFloor: Rate): Rate

}


object CouponPricing {

  trait Pricer[T, C] {

    def swapletPrice(termStructure: T, coupon: C): Rate

    def swapletRate(termStructure: T, coupon: C): Rate

    def capletPrice(termStructure: T, coupon: C, effectiveCap: Rate): Rate

    def capletRate(termStructure: T, coupon: C, effectiveCap: Rate): Rate

    def floorletPrice(termStructure: T, coupon: C, effectiveFloor: Rate): Rate

    def floorletRate(termStructure: T, coupon: C, effectiveFloor: Rate): Rate

  }

 // type Pricing = (Pricer, Coupon) => Rate

  trait Pricing[C] {
    def rate(coupon: C) = .swapletRate
  }

}