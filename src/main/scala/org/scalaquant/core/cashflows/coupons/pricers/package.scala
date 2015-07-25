package org.scalaquant.core.cashflows.coupons

import org.scalaquant.core.types.Rate
import org.scalaquant.core.instruments.options.Option

package object pricers {

  sealed trait Pricer

  trait FloatingRateCouponPricer {
    def swapletPrice: Rate

    def swapletRate: Rate

    def capletPrice(effectiveCap: Rate): Rate

    def capletRate(effectiveCap: Rate): Rate

    def floorletPrice(effectiveFloor: Rate): Rate

    def floorletRate(effectiveFloor: Rate): Rate
  }

  trait MeanRevertingPricer{
    def meanReversion: Double
  }

  class IborCouponPricer(val capletVolatility: OptionletVolatilityStructure) extends FloatingRateCouponPricer

  class CmsCouponPricer(val swaptionVolatility: SwaptionVolatilityStructure) extends FloatingRateCouponPricer


}
