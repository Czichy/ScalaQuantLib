package org.scalaquant.core.cashflows.coupons.pricers

import org.scalaquant.core.cashflows.coupons.{YoYInflationCoupon, Pricer, InflationCoupon}
import org.scalaquant.core.termstructures.OptionletVolatilityStructure
import org.scalaquant.core.types._

/**
  * Created by neo on 12/5/15.
  */

 class YoYInflationCouponPricer(capletVolatility: YoYOptionletVolatilitySurface)(implicit coupon: YoYInflationCoupon) extends Pricer[InflationCoupon](coupon){
  private val gearing = coupon.gearing
  private val spread = coupon.spread
  private val paymentDate = coupon.date
  private val rateCurve = coupon.index.yoyInflationTermStructure.nominalTermStructure
  def swapletPrice: Rate

  def swapletRate: Rate

  def capletPrice(effectiveCap: Rate): Rate

  def capletRate(effectiveCap: Rate): Rate

  def floorletPrice(effectiveFloor: Rate): Rate

  def floorletRate(effectiveFloor: Rate): Rate
}

class BlackYoYInflationCouponPricer(capletVolatility: OptionletVolatilityStructure)(implicit coupon: InflationCoupon)  extends Pricer[InflationCoupon](coupon){

}
