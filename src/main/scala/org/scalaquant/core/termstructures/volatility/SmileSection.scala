package org.scalaquant.core.termstructures.volatility


import org.joda.time.LocalDate
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.pricingengines.BlackFormula
import org.scalaquant.core.types.Rate
import org.scalaquant.core.instruments.options.Option


import org.scalaquant.math.Constants
import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._


/**
  * Created by neo on 11/12/15.
  */
abstract class SmileSection(val exerciseDate: LocalDate,
                   val daycounter: DayCountConvention,
                   val referenceDate: LocalDate,
                   val volatilityType: VolatilityType,
                   val shift: Rate) {

  require(exerciseDate >= referenceDate,
          s"expiry date ($exerciseDate) must be greater than reference date ($referenceDate)")

  val exerciseTime = daycounter.fractionOfYear(referenceDate, exerciseDate)

  def minStrike: Double

  def maxStrike: Double

  def variance(strike: Rate): Double

  def volatility(strike: Rate): Double

  def atmLevel: Double

  def optionPrice(strike: Rate,
                  optionType: Option.Type = Option.Call,
                  discount: Double = 1.0) = {

    val stdDev = if (strike + shift < Constants.QL_EPSILON)  0.2 else Math.sqrt(variance(strike))

    if (volatilityType == ShiftedLognormal){
       BlackFormula.apply(optionType, strike, atmLevel, Math.abs(stdDev), discount, shift)
    } else {
       BlackFormula.bachelier(optionType, strike, atmLevel, Math.sqrt(variance(strike)), discount)
    }
  }

  def vega(strike: Rate, discount: Double = 1.0): Option[Double] = {

    if (volatilityType == ShiftedLognormal)
      Some(BlackFormula.volDerivative(strike, atmLevel, Math.sqrt(variance(strike)), exerciseTime ,discount,shift * 0.01))
    else
      None
  }


  def digitalOptionPrice(strike: Rate,
                         optionType: Option.Type = Option.Call,
                         discount: Double = 1.0,
                         gap :Double = 1.0e-5) = {
    val m = if (volatilityType == ShiftedLognormal) -shift else -Double.MaxValue

    val kl = Math.max(strike-gap / 2.0 , m)
    val kr = kl + gap

    val optionPricing = optionPrice(_ , optionType, discount)

    (if (optionType == Option.Call) 1.0 else -1.0) * (optionPricing(kl) - optionPricing(kr)) / gap
  }


  def density(strike: Rate,
              discount: Double = 1.0,
              gap: Double = 1.0e-5) = {
    val m = if (volatilityType == ShiftedLognormal) -shift else -Double.MaxValue

    val kl = Math.max(strike-gap / 2.0 , m)
    val kr = kl + gap

    val digitalOptionPricing = digitalOptionPrice(_ , Option.Call, discount, gap)

    (digitalOptionPricing(kl) - digitalOptionPricing(kr)) / gap
  }

  def volatility(strike: Rate,
                 volatilityType: VolatilityType,
                 shift: Double = 0.0) = {
    if(volatilityType == this.volatilityType && shift ~= this.shift) {
      volatility(strike)
    }else{

    }

    val optionType = if(strike >= atmLevel) Option.Call else Option.Put
    val premium = optionPrice(strike,optionType)
    //val premiumAtm = optionPrice(atmLevel,optionType)

    if (volatilityType == ShiftedLognormal) {
      BlackFormula.impliedStdDev(optionType, strike, atmLevel, premium, 1.0, shift) / Math.sqrt(exerciseTime)
    } else {
      BlackFormula.bachelierimpliedVol(optionType, strike, atmLevel, exerciseTime, premium)
    }
  }
}
