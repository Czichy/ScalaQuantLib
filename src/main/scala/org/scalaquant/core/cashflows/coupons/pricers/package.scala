package org.scalaquant.core.cashflows.coupons

import org.scalaquant.core.types.Rate

sealed abstract class Pricer[+Coupon](coupon: Coupon){

    def swapletPrice: Rate

    def swapletRate: Rate

    def capletPrice(effectiveCap: Rate): Rate

    def capletRate(effectiveCap: Rate): Rate

    def floorletPrice(effectiveFloor: Rate): Rate

    def floorletRate(effectiveFloor: Rate): Rate

}


//abstract class CmsCouponPricer(val swaptionVolatility: SwaptionVolatilityStructure) extends Pricer[CMSCoupon]

