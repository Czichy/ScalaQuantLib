package org.scalaquant.core.termstructures

import java.time.LocalDate

import org.scalaquant.core.common.time.Frequency.Frequency
import org.scalaquant.core.common.time.Period
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.termstructures.inflation.Seasonality
import org.scalaquant.core.types.Rate

/**
  * Created by Neo Lin on 2016-01-17.
  */
trait YoYInflationTermStructure extends InflationTermStructure {
  def baseDate: LocalDate
  def dayCounter: DayCountConvention
  def baseRate: Rate
  def frequency: Frequency
  def indexIsInterpolated: Boolean
  def yTS: YieldTermStructure
  def seasonality: Seasonality
  def observationLag: Period
}
