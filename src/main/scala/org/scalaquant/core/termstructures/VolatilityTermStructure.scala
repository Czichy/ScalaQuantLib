package org.scalaquant.core.termstructures


import org.scalaquant.core.types.YearFraction

import java.time.LocalDate
import org.scalaquant.core.common.time.{TimeUnit, Period, BusinessDayConvention}
import org.scalaquant.core.termstructures.volatility.SmileSection
import org.scalaquant.core.types._

trait VolatilityTermStructure  extends TermStructure {

  def bdc: BusinessDayConvention

  def minStrike: Double =

  def maxStrike: Double

  def optionDateFromTenor(p: Period) = calendar.advance(referenceDate, p.days.toInt, TimeUnit.Days, bdc )

  protected def checkStrike(k: Double, extrapolate: Boolean) = {
    require(extrapolate || allowsExtrapolation || (k >= minStrike && k <= maxStrike),
      s"strike ($k) is outside the curve domain [${minStrike}, ${maxStrike}]")
  }

}

trait OptionletVolatilityStructure extends VolatilityTermStructure {


  def volatility(optionTenor: Period, strike: Rate, extrapolate: Boolean):Double = {
      volatility(optionDateFromTenor(optionTenor), strike, extrapolate)
    }

  def volatility(optionDate: LocalDate, strike: Rate, extrapolate: Boolean): Volatility = {
    checkRange(optionDate, extrapolate)
    checkStrike(strike, extrapolate)
    volatilityImpl(optionDate, strike)
  }

  def volatility(optionTime: YearFraction, strike: Rate, extrapolate: Boolean): Volatility = {
    checkRange(optionTime, extrapolate)
    checkStrike(strike, extrapolate)
    volatilityImpl(optionTime, strike)
  }

  def blackVariance(optionTenor: Period, strike: Rate, extrapolate: Boolean): Volatility  = {
    blackVariance(optionDateFromTenor(optionTenor), strike, extrapolate)
  }

  def blackVariance(optionDate: LocalDate, strike: Rate, extrapolate: Boolean): Volatility  = {
    val v = volatility(optionDate, strike, extrapolate)
    val t = timeFromReference(optionDate)
    v * v * t
  }

  def blackVariance(optionTime: YearFraction, strike: Rate, extrapolate: Boolean): Volatility  = {
    val v = volatility(optionTime, strike, extrapolate)
    v * v * optionTime
  }

  def smileSection(optionDate: LocalDate, extrapolate: Boolean = false): SmileSection = {
    checkRange(optionDate, extrapolate)
    smileSectionImpl(optionDate)
  }

  def smileSection(optionTime: YearFraction, extrapolate: Boolean = false): SmileSection = {
    checkRange(optionTime, extrapolate)
    smileSectionImpl(optionTime)
  }


  protected def volatilityImpl(optionDate: LocalDate, strike: Rate ): Volatility

  protected def volatilityImpl(optionTime: YearFraction , strike: Rate ) : Volatility

  protected def smileSectionImpl(optionDate: LocalDate): SmileSection

  protected def smileSectionImpl(optionTime: YearFraction): SmileSection

}

//case class SwaptionVolatilityStructure() extends VolatilityTermStructure
