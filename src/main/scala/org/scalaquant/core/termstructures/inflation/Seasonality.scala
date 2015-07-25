package org.scalaquant.core.termstructures.inflation

import org.joda.time.LocalDate
import org.scalaquant.common.time.Frequency
import org.scalaquant.common.time.Frequency._
import org.scalaquant.core.termstructures.InflationTermStructure

trait Seasonality {

  def correctZeroRate(date: LocalDate, r: Double, iTS: InflationTermStructure): Double

  def correctYoYRate(date: LocalDate, r: Double, iTS: InflationTermStructure): Double

  def isConsistent(iTS: InflationTermStructure): Boolean = true

}


class MultiplicativePriceSeasonality(val seasonalityBaseDate: LocalDate,
                                     val frequency: Frequency,
                                     val seasonalityFactors: Map[LocalDate, Double]) extends Seasonality{
    frequency match {
      case Semiannual | EveryFourthMonth | Quarterly | Bimonthly | Monthly | Biweekly | Weekly | Daily =>
        require(seasonalityFactors.size % frequency.value  == 0,
              s"For frequency $frequency require multiple of ${frequency.value} factors ${seasonalityFactors.size} were given.")
      case _ =>
        require(requirement = false, s"bad frequency specified: $frequency, only semi-annual through daily permitted.")
    }

  override def correctZeroRate(date: LocalDate, r: Double, iTS: InflationTermStructure): Double = ???

  override def correctYoYRate(date: LocalDate, r: Double, iTS: InflationTermStructure): Double = ???

  override def isConsistent(iTS: InflationTermStructure): Boolean = {
    if (frequency == Frequency.Daily) {
      true
    } else if (frequency.value == seasonalityFactors.size){
      true
    } else{

    // how many years do you need to test?
    val nTest = seasonalityFactors.size / frequency.value
    // ... relative to the start of the inflation curve
    val lim = period(iTS.baseDate, iTS.frequency)
    val curveBaseDate = lim._2
    val factorBase = seasonalityFactor(curveBaseDate)

    val eps = 0.00001
    for (Size i = 1; i < nTest; i++) {
      val factorAt = seasonalityFactor(curveBaseDate.plusYears(i))
      require(Math.abs(factorAt-factorBase)<eps,
        "seasonality is inconsistent with inflation term structure, factors "
        << factorBase << " and later factor " << factorAt << ", " << i << " years later from inflation curve "
        <<" with base date at " << curveBaseDate);
    }

    }

  }
}