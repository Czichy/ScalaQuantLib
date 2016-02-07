package org.scalaquant.core.cashflows.coupons.pricers

import java.time.LocalDate
import org.scalaquant.core.cashflows.coupons.pricers.{Pricer, Pricing}
import org.scalaquant.core.cashflows.coupons.{IBORCoupon, Coupon, Pricer}
import org.scalaquant.core.instruments.options.Option
import org.scalaquant.core.termstructures.OptionletVolatilityStructure
import org.scalaquant.core.types._

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

final case class BlackIborCouponPricer(capletVolatility: OptionletVolatilityStructure, coupon: IBORCoupon){
    private val accrualPeriod = coupon.accrualPeriod(capletVolatility.dc)
    require(accrualPeriod != 0.0, "null accrual period")


    private val rateCurve = coupon.index.forwardingTermStructure

    private val discount = if (coupon.date > rateCurve.referenceDate) rateCurve.discount(coupon.date) else 1.0
    private val spreadLegValue = coupon.spread * accrualPeriod * discount

    protected def optionletPrice(optionType: Option.Type, effStrike: Rate): Rate = ???

    protected def adjustedFixing(fixing: Rate = coupon.indexFixing(LocalDate.now())) = {
      val date1 = coupon.fixingDate
      val referenceDate = capletVolatility.referenceDate
      if (date1 <= referenceDate) {
        fixing
      } else {
        // see Hull, 4th ed., page 550
        val date2 = coupon.index.valueDate(date1)
        val date3 = coupon.index.maturityDate(date2)
        val tau = coupon.index.dayCounter.fractionOfYear(date2, date3)
        val variance = capletVolatility.blackVariance(date1, fixing)
        val adjustement = fixing * fixing * variance * tau / (1.0 + fixing * tau)

        fixing + adjustement

      }
    }

    def swapletPrice = coupon.gearing * adjustedFixing() * accrualPeriod * discount + spreadLegValue

    def swapletRate = swapletPrice / (accrualPeriod * discount)

    def capletPrice(effectiveCap: Rate) = coupon.gearing * optionletPrice(Option.Call, effectiveCap)

    def capletRate(effectiveCap: Rate) = capletPrice(effectiveCap) / (accrualPeriod * discount)

    def floorletPrice(effectiveFloor: Rate) = coupon.gearing *  optionletPrice(Option.Put, effectiveFloor)

    def floorletRate(effectiveFloor: Rate) = floorletPrice(effectiveFloor) / (accrualPeriod * discount)

  }
}


object BlackIborCouponPricer extends VolatilityStructure[]

