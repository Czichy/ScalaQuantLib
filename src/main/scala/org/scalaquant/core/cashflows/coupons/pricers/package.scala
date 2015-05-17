package org.scalaquant.core.cashflows.coupons

/**
 * Created by neo on 2015-05-10.
 */
package object pricers {

  trait FloatingRateCouponPricer {
    def swapletPrice: Double
    def swapletRate: Double
    def capletPrice(effectiveCap: Double): Double
    def capletRate(effectiveCap: Double): Double
    def floorletPrice(effectiveFloor: Double): Double
    def floorletRate(effectiveFloor: Double): Double
  }

  trait MeanRevertingPricer{
    def meanReversion: Double
  }
  class IborCouponPricer(capletVolatility: OptionletVolatilityStructure) extends FloatingRateCouponPricer
}
