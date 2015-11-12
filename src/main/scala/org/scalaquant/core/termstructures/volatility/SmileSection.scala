package org.scalaquant.core.termstructures.volatility


import org.joda.time.LocalDate
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.types.Rate
import org.scalaquant.core.instruments.options.Option
/**
  * Created by neo on 11/12/15.
  */
class SmileSection(val exerciseDate: LocalDate,
                   val daycounter: DayCountConvention,
                   val referenceDate: LocalDate,
                   val volatilityType: VolatilityType,
                   val shift: Rate) {

  def minStrike: Double
  def maxStrike: Double
  def variance(strike: Rate): Double
  def volatility(strike: Rate): Double
  def atmLevel: Double

  //virtual Time exerciseTime() const { return exerciseTime_; }
  def optionPrice(strike: Rate , optionType: Option.OptionType  = Option.Call, Real discount=1.0) const;
  def digitalOptionPrice(Rate strike, Option::Type type = Option::Call, Real discount=1.0, Real gap=1.0e-5) const;
  def vega(Rate strike, Real discount=1.0) const;
  def density(Rate strike, Real discount=1.0, Real gap=1.0E-4) const;
  def volatility(Rate strike, VolatilityType type, Real shift=0.0) const;
}
