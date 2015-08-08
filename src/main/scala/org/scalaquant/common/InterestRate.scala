package org.scalaquant.common

import org.joda.time.LocalDate
import org.scalaquant.common.Compounding._

import org.scalaquant.common.time.Frequency._
import org.scalaquant.common.time.daycounts._

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

case class InterestRate(rate: Double, dc: DayCountConvention, comp: Compounding, freq: Frequency) {

  if (comp == Compounded || comp == SimpleThenCompounded)
    require(freq!= Once && freq!= NoFrequency, "frequency not allowed for this interest rate")

  def discountFactor(time: Double): Double = 1.0 / compoundFactor(time)

  def discountFactor(d1: LocalDate,
                     d2: LocalDate,
                     refStart: Option[LocalDate] = None,
                     refEnd: Option[LocalDate] = None): Double  = {
    require(d2 >= d1, s"date1($d1) later than date2($d2)")

    discountFactor(dc.fractionOfYear(d1, d2, refStart, refEnd))
  }

  def compoundFactor(time: Double): Double = {
    require(time >= 0.0, "negative time not allowed")

    def simple = 1.0 * rate * time

    def compounded = Math.pow(1.0 + rate / freq.value, freq.value * time)

    comp match {
      case Simple => simple
      case Compounded => compounded
      case Continuous => Math.exp(rate * time)
      case SimpleThenCompounded => if (time <= 1.0/freq.value) simple else compounded
    }
  }

  def compoundFactor(d1: LocalDate,
                     d2: LocalDate,
                     refStart: Option[LocalDate] = None,
                     refEnd: Option[LocalDate] = None): Double = {
    require(d2 >= d1, s"date1($d1) later than date2($d2)")

    compoundFactor(dc.fractionOfYear(d1, d2, refStart, refEnd))
  }

  def equivalentRate(comp: Compounding, freq: Frequency, time: Double): InterestRate = {
    InterestRate.impliedRate(compoundFactor(time),dc,comp,freq,time)
  }

  def equivalentRate(resultDc: DayCountConvention,
                     comp: Compounding,
                     freq: Frequency,
                     date1: LocalDate,
                     date2: LocalDate,
                     refStart: Option[LocalDate] = None,
                       refEnd: Option[LocalDate] = None): InterestRate  = {
    require(date2 >= date1, s"date1($date1) later than date2($date2)")

    val compound = compoundFactor(dc.fractionOfYear(date1, date2, refStart, refEnd))

    InterestRate.impliedRate(compound, dc, comp, freq, resultDc.fractionOfYear(date1,date2, refStart, refEnd))
  }

  override def toString: String = {
    val compounding = comp match {
      case Simple => Simple
      case Compounded => freq
      case Continuous => Continuous
      case SimpleThenCompounded =>  "Simple Compounding up to " + (12 / freq.value) + " months, then " + freq
    }
    rate + " " + dc + " " + compounding + "Compounding"
  }
}

object InterestRate{

  def impliedRate(compound: Double,
                  dc: DayCountConvention,
                  comp: Compounding,
                  freq: Frequency,
                  time: Double): InterestRate = {

    require(compound > 0.0, "positive compound factor required")
    require(compound == 1.0 && time >= 0.0, s"non negative time ($time) required when compound is 1")
    require(compound != 1.0 && time > 0.0, s"positive time ($time) required when compound is not 1")

    def simpleRate = (compound - 1.0) / time

    def compoundedRate = (Math.pow(compound, 1.0/ freq.value.toDouble * time) - 1.0) * freq.value.toDouble

    val rate = if (compound == 1.0) {
      0.0
    } else {
      comp match {
        case Simple => simpleRate
        case Compounded => compoundedRate
        case Continuous => Math.log(compound) / time
        case SimpleThenCompounded => if (time <= 1.0 / freq.value.toDouble) simpleRate else compoundedRate
      }
    }

    InterestRate(rate, dc, comp, freq)
  }

  def impliedRate(compound: Double,
                  dc: DayCountConvention,
                  comp: Compounding,
                  freq: Frequency,
                  date1: LocalDate,
                  date2: LocalDate,
                  refStart: Option[LocalDate] = None,
                  refEnd: Option[LocalDate] = None): InterestRate = {
    require(date2 >= date1, s"date1($date1) later than date2($date2)")

    impliedRate(compound, dc, comp, freq, dc.fractionOfYear(date1, date2, refStart, refEnd))
  }
}
