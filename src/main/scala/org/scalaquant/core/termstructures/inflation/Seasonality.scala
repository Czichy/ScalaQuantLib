package org.scalaquant.core.termstructures.inflation

import org.joda.time.LocalDate
import org.scalaquant.core.common.time.JodaDateTimeHelper._
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.common.time.{Frequency, TimeUnit, Period}
import org.scalaquant.core.common.time.Frequency._
import org.scalaquant.core.termstructures.InflationTermStructure
import org.scalaquant.core.types.Rate

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

  override def correctZeroRate(date: LocalDate,
                               r: Double, iTS: InflationTermStructure): Double = {
    val lim = InflationTermStructure.inflationPeriod(iTS.baseDate, iTS.frequency)
    val curveBaseDate = lim._2

    seasonalityCorrection(r, d, iTS.dayCounter, curveBaseDate, true)
  }

  override def correctYoYRate(date: LocalDate, r: Double, iTS: InflationTermStructure): Double = ???

//  override def isConsistent(iTS: InflationTermStructure): Boolean = {
//    if (frequency == Frequency.Daily) {
//      true
//    } else if (frequency.value == seasonalityFactors.size){
//      true
//    } else{
//
//    // how many years do you need to test?
//    val nTest = seasonalityFactors.size / frequency.value
//    // ... relative to the start of the inflation curve
//    val lim = period(iTS.baseDate, iTS.frequency)
//    val curveBaseDate = lim._2
//    val factorBase = seasonalityFactor(curveBaseDate)
//
//    val eps = 0.00001
//
//    for (Size i = 1; i < nTest; i++) {
//      val factorAt = seasonalityFactor(curveBaseDate.plusYears(i))
//      require(Math.abs(factorAt-factorBase)<eps,
//        s"seasonality is inconsistent with inflation term structure, factors $factorBase and later factor " +
//          s"$factorAt ,  $i years later from inflation curve with base date at $curveBaseDate")
//    }
//
//    }
//
//    true
//
//  }

  def seasonalityFactor(to: LocalDate): Double = {
    val from = seasonalityBaseDate
    val factorFrequency = frequency
    val nFactors = seasonalityFactors.size
    val factorPeriod = Period(factorFrequency)
    Size which = 0;
    if (from==to) {
      which = 0;
    } else {
      // days, weeks, months, years are the only time unit possibilities
      Integer diffDays = std::abs(to - from);  // in days
      Integer dir = 1;
      if(from > to)dir = -1;
      Integer diff;
      if (factorPeriod.units() == Days) {
        diff = dir*diffDays;
      } else if (factorPeriod.units() == Weeks) {
        diff = dir * (diffDays / 7);
      } else if (factorPeriod.units() == Months) {
        std::pair<Date,Date> lim = inflationPeriod(to, factorFrequency);
        diff = diffDays / (31*factorPeriod.length());
        Date go = from + dir*diff*factorPeriod;
        while ( !(lim.first <= go && go <= lim.second) ) {
          go += dir*factorPeriod;
          diff++;
        }
        diff=dir*diff;
      } else if (factorPeriod.units() == Years) {
        QL_FAIL("seasonality period time unit is not allowed to be : " << factorPeriod.units());
      } else {
        QL_FAIL("Unknown time unit: " << factorPeriod.units());
      }
      // now adjust to the available number of factors, direction dependent

      if (dir==1) {
        which = diff % nFactors;
      } else {
        which = (nFactors - (-diff % nFactors)) % nFactors;
      }
    }

    return seasonalityFactors()[which];
  }
  def seasonalityCorrection(rate: Rate,
                            atDate: LocalDate,
                            dc: DayCountConvention,
                            curveBaseDate: LocalDate,
                            isZeroRate: Boolean): Rate = {
    val factorAt = this.seasonalityFactor(atDate)

    //Getting seasonality correction for either ZC or YoY
    val f =
      if (isZeroRate) {
        val factorBase = this.seasonalityFactor(curveBaseDate)
        val seasonalityAt = factorAt / factorBase
        val timeFromCurveBase = dc.fractionOfYear(curveBaseDate, atDate)

        Math.pow(seasonalityAt, 1/timeFromCurveBase)
      }
      else {
        val factor1Ybefore = this.seasonalityFactor(atDate - Period(1, TimeUnit.Years))

        factorAt / factor1Ybefore
      }

     (rate + 1) * f - 1
  }

}