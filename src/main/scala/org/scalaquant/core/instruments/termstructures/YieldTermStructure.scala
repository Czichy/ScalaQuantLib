package org.scalaquant.core.instruments.termstructures

import org.joda.time.LocalDate
import org.scalaquant.core.common.{InterestRate, Compounding}
import org.scalaquant.core.common.time.{Period, Frequency}
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.instruments.Quote


abstract class YieldTermStructure(private var _referenceDate: LocalDate,
                                  override val calendar: BusinessCalendar,
                                  override val dc: DayCountConvention,
                                  jumps: Seq[(Quote[Double], LocalDate)])
  extends TermStructure(_referenceDate, calendar, dc){

  private val jumpWithTimes: Seq[(Quote[Double], LocalDate, Double)] =  jumps.map(old =>(old._1, old._2, timeFromReference(old._2)))

  def discount(date: LocalDate, extrapolate: Boolean = false): Double = discount(this.timeFromReference(date), extrapolate)

  def discount(time: Double, extrapolate: Boolean = false): Double = {
    val jumpEffect = if (jumpWithTimes.isEmpty)
      1.0
    else
      jumpWithTimes.filter( jump => jump._3 < time && 0 < jump._3 )
                   .filter(_._1.isValid)
                   .map(_._1.value)
                   .filter( x => x > 0.0 && x <= 1.0 )
                   .product

    discountImpl(time) * jumpEffect
  }
  protected def discountImpl(time: Double): Double

  def zeroRate(date: LocalDate,
               resultDC: DayCountConvention,
               comp: Compounding,
               freq: Frequency,
               extrapolate: Boolean = false): InterestRate = {
    if (date == referenceDate){
      
    }
  }

  def zeroRate(time: Double,
               comp: Compounding,
               freq: Frequency,
               extrapolate: Boolean = false): InterestRate

  def forwardRate(date1: LocalDate, date2: LocalDate,
               resultDC: DayCountConvention,
               comp: Compounding,
               freq: Frequency,
               extrapolate: Boolean = false): InterestRate

  def forwardRate(date: LocalDate, period: Period,
                  resultDC: DayCountConvention,
                  comp: Compounding,
                  freq: Frequency,
                  extrapolate: Boolean = false): InterestRate

  def forwardRate(time1: Double, time2: Double,
                  comp: Compounding,
                  freq: Frequency,
                  extrapolate: Boolean = false): InterestRate


}
