package org.scalaquant.core.cashflows.coupons.pricers

import org.scalaquant.core.cashflows.coupons.FloatingRateCoupon
import org.scalaquant.core.instruments.options.Option
import org.scalaquant.core.types._


final class BlackIborCouponPricer(capletVolatility: OptionletVolatilityStructure) extends IborCouponPricer(capletVolatility){

  protected def optionletPrice(optionType: Option.Type, effStrike: Rate): Rate

  override def swapletPrice: Double = gearing *  adjustedFixing * accrualPeriod * discount + spreadLegValue

  override def swapletRate: Double = swapletPrice / (accrualPeriod * discount)

  override def capletPrice(effectiveCap: Rate): Rate = optionletPrice(Option.Call, effectiveCap) * capletPrice

  override def capletRate(effectiveCap: Rate): Rate = capletPrice(effectiveCap) / (accrualPeriod * discount)

  override def floorletPrice(effectiveFloor: Rate): Rate = optionletPrice(Option.Put, effectiveFloor) * floorletPrice

  override def floorletRate(effectiveFloor: Rate): Rate = floorletPrice(effectiveFloor) / (accrualPeriod_*discount_)
}

object BlackIborCouponPricer{
  def initialize(coupon: FloatingRateCoupon): BlackIborCouponPricer = {

  }


}