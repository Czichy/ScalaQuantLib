package org.scalaquant.core.termstructures

import org.joda.time.LocalDate
import org.scalaquant.core.common.time.{TimeUnit, Period, BusinessDayConvention}
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.types._

abstract class VolatilityTermStructure( settlementDays: Int,
                                        referenceDate: LocalDate,
                                        calendar: BusinessCalendar,
                                        dc: DayCountConvention,
                                        val bdc: BusinessDayConvention )
  extends TermStructure(settlementDays, referenceDate, calendar, dc){

  def minStrike: Double

  def maxStrike: Double

  protected def checkStrike(k: Double, extrapolate: Boolean) = {
    require(extrapolate || allowsExtrapolation || (k >= minStrike && k <= maxStrike),
      s"strike ($k) is outside the curve domain [${minStrike}, ${maxStrike}]")
  }

  def optionDateFromTenor(p: Period) = calendar.advance(referenceDate, p.days.toInt, TimeUnit.Days, bdc )

}

case class OptionletVolatilityStructure(override val settlementDays: Int,
                                        override val referenceDate: LocalDate,
                                        override val calendar: BusinessCalendar,
                                        override val dc: DayCountConvention,
                                        override val bdc: BusinessDayConvention)
  extends VolatilityTermStructure(settlementDays, referenceDate, calendar, dc, bdc){

  type Volatility = (Any, Rate, Boolean) => Double
  type BlackVariance = Volatility

  protected smileSectionImpl(optionDate: LocalDate): SmileSection

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

case class SwaptionVolatilityStructure extends VolatilityTermStructure
