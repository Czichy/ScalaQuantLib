package org.scalaquant.core.termstructures.volatility

import java.time.LocalDate

import org.scalaquant.core.common.time.Period
import org.scalaquant.core.termstructures.VolatilityTermStructure
import org.scalaquant.core.types._

/**
  * Created by neo on 2/5/16.
  */
trait CapFloorTermVolatilityStructure extends VolatilityTermStructure {


  def volatility(optionTenor: Period, strike: Rate, extrapolate: Boolean):Double = {
    volatility(optionDateFromTenor(optionTenor), strike, extrapolate)
  }

  def volatility(optionDate: LocalDate, strike: Rate, extrapolate: Boolean): Volatility = {
    checkRange(optionDate, extrapolate)
    volatilityImpl(timeFromReference(optionDate), strike)
  }
  def volatility(optionTime: YearFraction, strike: Rate, extrapolate: Boolean): Volatility = {
    checkRange(optionTime, extrapolate)
    checkStrike(strike, extrapolate)
    volatilityImpl(optionTime, strike)
  }

  protected def volatilityImpl(optionTime: YearFraction , strike: Rate ) : Volatility

}
