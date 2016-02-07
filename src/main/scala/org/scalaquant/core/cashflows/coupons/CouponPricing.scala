package org.scalaquant.core.cashflows.coupons

import org.scalaquant.core.termstructures.VolatilityTermStructure
import org.scalaquant.core.types._

/**
  * Created by neo on 2/3/16.
  */
object CouponPricing {

  type Pricing = VolatilityTermStructure => Coupon => Rate

  type Pricer = (VolatilityTermStructure, Coupon)

  trait PricingOps[P]{

    val swapletPrice: Pricing

    val swapletRate: Pricing

    val capletPrice: Rate => Pricing

    val capletRate: Rate => Pricing

    val floorletPrice: Rate => Pricing

    val floorletRate: Rate => Pricing
  }

  trait CouponPricingOps[P]{
    def swapletPrice: Rate

    def swapletRate: Rate

    def capletPrice(effectiveCap: Rate): Rate

    def capletRate(effectiveCap: Rate): Rate

    def floorletPrice(effectiveFloor: Rate): Rate

    def floorletRate(effectiveFloor: Rate): Rate
  }

  object ImplicitsOps{

    implicit class CouponPricingOpsClass[P <: Pricer](val p: P)(implicit ops: PricingOps[P]) extends CouponPricingOps[P] {

      val (termStructure, coupon) = p

      def swapletPrice: Rate = ops.swapletPrice(termStructure)(coupon)

      def swapletRate: Rate = ops.swapletRate(termStructure)(coupon)

      def capletPrice(effectiveCap: Rate): Rate = ops.capletPrice(effectiveCap)(termStructure)(coupon)

      def capletRate(effectiveCap: Rate): Rate = ops.capletRate(effectiveCap)(termStructure)(coupon)

      def floorletPrice(effectiveFloor: Rate): Rate = ops.floorletPrice(effectiveFloor)(termStructure)(coupon)

      def floorletRate(effectiveFloor: Rate): Rate = ops.floorletRate(effectiveFloor)(termStructure)(coupon)
    }


  }
}
