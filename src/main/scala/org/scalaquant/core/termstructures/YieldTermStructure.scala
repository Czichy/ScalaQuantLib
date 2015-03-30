package org.scalaquant.core.termstructures

import org.joda.time.LocalDate
import org.scalaquant.core.common.{InterestRate, Compounding}
import org.scalaquant.core.common.time.{Period, Frequency}
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.common.time.JodaDateTimeHelper._
import org.scalaquant.core.quotes.Quote


abstract class YieldTermStructure(private var _referenceDate: LocalDate,
                                  override val calendar: BusinessCalendar,
                                  override val dc: DayCountConvention,
                                  jumps: Seq[(Quote, LocalDate)])
  extends TermStructure(_referenceDate, calendar, dc){

  private var _jumps = jumps

  def this(settlementDays: Int,
           calendar: BusinessCalendar,
           dc: DayCountConvention,
           jumps: Seq[(Quote, LocalDate)])={
    this(settlementDays,calendar,dc)
    this._jumps = jumps
  }
  private val _jumpWithTimes: Seq[(Quote, Double)] =  _jumps.map(old =>(old._1, timeFromReference(old._2)))

  def discount(date: LocalDate, extrapolate: Boolean = false): Double = discount(this.timeFromReference(date), extrapolate)

  def discount(time: Double, extrapolate: Boolean = false): Double = {
    val jumpEffect = if (_jumpWithTimes.isEmpty)
      1.0
    else
      _jumpWithTimes.filter( jump => jump._2 < time && 0 < jump._2 && jump._1.isValid )
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
    if (date == referenceDate)
      InterestRate.impliedRate(1.0/discount(YieldTermStructure.dt,extrapolate),resultDC,comp,freq,YieldTermStructure.dt)
    else
      InterestRate.impliedRate(1.0/discount(date,extrapolate),resultDC,comp,freq,referenceDate,date,date)
  }

  def zeroRate(time: Double,
               comp: Compounding,
               freq: Frequency,
               extrapolate: Boolean = false): InterestRate = {
    val t = if (time==0.0) YieldTermStructure.dt else time
    InterestRate.impliedRate(1.0/discount(t, extrapolate), dc, comp, freq, t)
  }

  def forwardRate(date1: LocalDate, date2: LocalDate,
               resultDC: DayCountConvention,
               comp: Compounding,
               freq: Frequency,
               extrapolate: Boolean = false): InterestRate = {
    require(date1 <= date2, "date1 later than date2")
    if (date1 == date2) {
      val t1 = Math.max(timeFromReference(date1)-YieldTermStructure.dt/2.0, 0.0)
      val t2 = t1 + YieldTermStructure.dt
      InterestRate.impliedRate(discount(t1, true) / discount(t2, true), dc, comp, freq, YieldTermStructure.dt)
    } else {
      InterestRate.impliedRate(discount(date1, extrapolate)/discount(date2, extrapolate), dc, comp, freq, date1, date2, date2)
    }
  }

  def forwardRate(date: LocalDate, period: Period,
                  resultDC: DayCountConvention,
                  comp: Compounding,
                  freq: Frequency,
                  extrapolate: Boolean = false): InterestRate = {
    forwardRate(date, date.plusDays(period.days.toInt), dc, comp, freq, extrapolate)
  }

  def forwardRate(time1: Double, time2: Double,
                  comp: Compounding,
                  freq: Frequency,
                  extrapolate: Boolean = false): InterestRate = {
    require(time1 <= time2, "time1 bigger than time2")
    val (interval, compound) = if (time1 == time2) {
      val t1 = Math.max(time1-YieldTermStructure.dt/2.0, 0.0)
      val t2 = t1 + YieldTermStructure.dt
      (t2 - t1, discount(t1, true) / discount(t2, true))
    } else {
      (time2 - time1, discount(time1, extrapolate) / discount(time2, extrapolate))
    }
    InterestRate.impliedRate(compound, dc, comp, freq, interval)
  }

  def jumpDates: Seq[LocalDate] = _jumps.map(_._2)
  def jumpTimes: Seq[Double] = _jumpWithTimes.map(_._2)

  override def isEmpty: Boolean = jumps.isEmpty

  override def nonEmpty: Boolean = jumps.nonEmpty
}

object YieldTermStructure{
  val dt: Double = 0.0001
}