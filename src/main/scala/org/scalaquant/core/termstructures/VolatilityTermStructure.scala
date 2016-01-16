package org.scalaquant.core.termstructures


import org.scalaquant.core.types.YearFraction

import java.time.LocalDate
import org.scalaquant.core.common.time.{TimeUnit, Period, BusinessDayConvention}
import org.scalaquant.core.termstructures.volatility.SmileSection
import org.scalaquant.core.types._

trait VolatilityTermStructure { self: TermStructure =>

  def bdc: BusinessDayConvention

  def minStrike: Double

  def maxStrike: Double

  def optionDateFromTenor(p: Period) = calendar.advance(referenceDate, p.days.toInt, TimeUnit.Days, bdc )

  protected def checkStrike(k: Double, extrapolate: Boolean) = {
    require(extrapolate || allowsExtrapolation || (k >= minStrike && k <= maxStrike),
      s"strike ($k) is outside the curve domain [${minStrike}, ${maxStrike}]")
  }

}

trait OptionletVolatilityStructure{ self: VolatilityTermStructure =>

  type Volatility = (Any, Rate, Boolean) => Double
  type BlackVariance = Volatility

  protected def volatilityImpl(optionDate: LocalDate, strike: Rate )
  //! implements the actual volatility calculation in derived classes
  protected def volatilityImpl(optionTime: YearFraction , strike: Rate )
  protected def smileSectionImpl(optionDate: LocalDate): SmileSection

  val volatility: Volatility = {
    case (optionTenor: Period, strike, extrapolate) =>
      volatility(optionDateFromTenor(optionTenor), strike, extrapolate)
    case (optionDate: LocalDate, strike, extrapolate) =>
      checkRange(optionDate, extrapolate)
      checkStrike(strike, extrapolate)
      volatilityImpl(optionDate, strike)
    case (optionTime: YearFraction, strike, extrapolate) =>
      checkRange(optionTime, extrapolate)
      checkStrike(strike, extrapolate)
      volatilityImpl(optionTime, strike)
    case (_,_,_) => Double.NaN
  }

  val blackVariance: BlackVariance ={
    case (optionTenor: Period, strike, extrapolate) =>
      blackVariance(optionDateFromTenor(optionTenor), strike, extrapolate)
    case (optionDate: LocalDate, strike, extrapolate) =>
      val v = volatility(optionDate, strike, extrapolate)
      val t = timeFromReference(optionDate);
      v * v * t
    case (optionTime: YearFraction, strike, extrapolate) =>
      val v = volatility(optionTime, strike, extrapolate)
      v * v * optionTime;
    case (_,_,_) => Double.NaN
  }


  //! returns the smile for a given option tenor
  def smileSection(const Period& optionTenor, bool extr = false) const;
  //! returns the smile for a given option date
  def smileSection(const Date& optionDate, bool extr = false) const;
  //! returns the smile for a given option time
  def smileSection(Time optionTime, bool extr = false) const;
}

//case class SwaptionVolatilityStructure() extends VolatilityTermStructure
