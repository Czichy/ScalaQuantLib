package org.scalaquant.core.termstructures

import org.joda.time.LocalDate
import org.scalaquant.core.common.Compounding._
import org.scalaquant.core.common.InterestRate
import org.scalaquant.core.common.time.Frequency._
import org.scalaquant.core.common.time.Period
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention

import org.scalaquant.core.quotes.{ValidQuote, Quote}
import org.scalaquant.core.termstructures.YieldTermStructure._
import org.scalaquant.core.types._

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

object YieldTermStructure{
  case class JumpDate(quote: ValidQuote, date: LocalDate)
  case class JumpTime(quote: ValidQuote, time: YearFraction)
  val dt: YearFraction = 0.0001
}

abstract class YieldTermStructure(override val settlementDays: Int,
                                  override val referenceDate: LocalDate,
                                  override val calendar: BusinessCalendar,
                                  override val dc: DayCountConvention,
                                  val jumpDates: Seq[JumpDate] = Nil)
  extends TermStructure(settlementDays, referenceDate, calendar, dc) {

  val jumpTimes: Seq[JumpTime] = {
    jumpDates.map(jump => JumpTime(jump.quote, timeFromReference(jump.date)))
  }

  def discount(date: LocalDate, extrapolate: Boolean = false): DiscountFactor = {
    discount(this.timeFromReference(date), extrapolate)
  }

  def discount(time: YearFraction, extrapolate: Boolean = false): DiscountFactor = {
    checkRange(time, extrapolate)
    jumpTimes match {
      case Nil => discountImpl(time)
      case x => discountImpl(time) * jumpTimes
        .filter(jump => jump.time > 0.0 && jump.time < time)
        .filter(jump => jump.quote.value > 0.0 && jump.quote.value <= 1.0)
        .map(_.quote.value).product
    }
  }

  protected def discountImpl(time: YearFraction): DiscountFactor

  def zeroRate(date: LocalDate,
               resultDC: DayCountConvention,
               comp: Compounding,
               freq: Frequency,
               extrapolate: Boolean = false): InterestRate = {
    if (date == referenceDate)
      InterestRate.impliedRate(1.0 / discount(YieldTermStructure.dt, extrapolate), resultDC, comp, freq, YieldTermStructure.dt)
    else
      InterestRate.impliedRate(1.0 / discount(date, extrapolate), resultDC, comp, freq, referenceDate, date)
  }

  def zeroRate(time: Double,
               comp: Compounding,
               freq: Frequency,
               extrapolate: Boolean = false): InterestRate = {
    val t = if (time == 0.0) YieldTermStructure.dt else time
    InterestRate.impliedRate(1.0 / discount(t, extrapolate), dc, comp, freq, t)
  }

  def forwardRate(date1: LocalDate, date2: LocalDate,
                  resultDC: DayCountConvention,
                  comp: Compounding,
                  freq: Frequency,
                  extrapolate: Boolean = false): InterestRate = {
    require(date1 <= date2, "date1 later than date2")
    if (date1 == date2) {
      val t1 = Math.max(timeFromReference(date1) - YieldTermStructure.dt / 2.0, 0.0)
      val t2 = t1 + YieldTermStructure.dt
      InterestRate.impliedRate(discount(t1, extrapolate = true) / discount(t2, extrapolate = true), dc, comp, freq, YieldTermStructure.dt)
    } else {
      InterestRate.impliedRate(discount(date1, extrapolate) / discount(date2, extrapolate), dc, comp, freq, date1, date2)
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
      val t1 = Math.max(time1 - YieldTermStructure.dt / 2.0, 0.0)
      val t2 = t1 + YieldTermStructure.dt
      (t2 - t1, discount(t1, extrapolate = true) / discount(t2, extrapolate = true))
    } else {
      (time2 - time1, discount(time1, extrapolate) / discount(time2, extrapolate))
    }

    InterestRate.impliedRate(compound, dc, comp, freq, interval)
  }
}