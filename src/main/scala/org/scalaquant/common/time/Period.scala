package org.scalaquant.common.time

import org.scalaquant.common.time.Frequency._
import org.scalaquant.common.time.TimeUnit._

object Period {
  val Empty = Period()
  val Unknown = Period(999)

  import Frequency._

  def apply(freq: Frequency): Period = freq.value match {
      case NoFrequency.value => Empty
      case Once.value => Period(0, Years)
      case Annual.value => Period(1, Years)
      case Semiannual.value | EveryFourthMonth.value |
           Quarterly.value | Bimonthly.value |
           Monthly.value => Period( 12 / freq.value, Months )
      case EveryFourthWeek.value | Biweekly.value | Weekly.value => Period( 52 / freq.value, Weeks )
      case Daily.value => Period(1)
      case _ => Unknown
    }

}

case class Period(length: Int = 0, units: TimeUnit = Days) {
  
  def normalize: Period = {
    if (length != 0) {
      units match {
        case Days =>
          if (length % 7 == 0) Period(length / 7, Weeks) else this
        case Months =>
          if (length % 12 == 0) Period(length / 12, Years) else this
        case _ => this
      }
    } else {
      this
    }
  }

  def unary_- = Period(-length, units)

  def *(n: Int) = Period(n * length, units)

  def *:(n: Int) = *(n)

  //def *(other: Period) =
  //    def /(other: Period) =
  //    def >(other: Period) =
  //    def <(other: Period) =
  private def descriptions(toDays: => String)(toWeeks: => String)(toMonths: => String)(toYears: => String): String = {
    units match {
      case Days => toDays
      case Weeks => toWeeks
      case Months => toMonths
      case Years => toYears
    }
  }

  val shortDescription: String =
    descriptions { //toDays
      val week = length / 7 match {
        case 0 => ""
        case x => x + "W"
      }
      val day = length % 7 match {
        case 0 => ""
        case x => x + "D"
      }
      week + day
    } { //toWeeks
      length + "W"
    } { //toMonths
      val year = length / 12 match {
        case 0 => ""
        case x => x + "Y"
      }
      val month = length % 12 match {
        case 0 => ""
        case x => x + "M"
      }
      year + month
    } { //toYears
      length + "Y"
    }

  val longDescription: String =
    descriptions { //toDays
      val week = length / 7 match {
        case 0 => ""
        case 1 => "1 week "
        case x => x + " weeks "
      }
      val day = length % 7 match {
        case 0 => ""
        case 1 => "1 day"
        case x => x + " days"
      }
      (week + day).trim
    } { //toWeeks
      if (length == 1) s"1 week" else length + " weeks"
    } { //toMonths
      val year = length / 12 match {
        case 0 => ""
        case 1 => "1 year "
        case x => x + " years "
      }
      val month = length % 12 match {
        case 0 => ""
        case 1 => "1 month"
        case x => x + " months"
      }
      (year + month).trim
    }{ //toYears
      if (length == 1) "1 year" else length + " years"
    }


  val frequency: Frequency =
    Math.abs(length) match {
      case 0 =>
        units match {
          case Years => Once
          case _ => NoFrequency
        }
      case l =>
        units match {
          case Years =>
            l match {
              case 1 => Annual
              case _ => OtherFrequency
            }
          case Months =>
            l match {
              case 6 => Semiannual
              case 4 => EveryFourthMonth
              case 3 => Quarterly
              case 2 => Bimonthly
              case 1 => Monthly
              case _ => OtherFrequency
            }
          case Weeks =>
            l match {
              case 1 => Weekly
              case 2 => Biweekly
              case 4 => EveryFourthWeek
              case _ => OtherFrequency
            }
          case Days =>
            l match {
              case 1 => Daily
              case _ => OtherFrequency
            }
        }
    }



  private def unitsMatching(matches: TimeUnit => Double): Double =
    if (length == 0 ) 0.0 else matches.apply(units)

  val years: Double =
    unitsMatching {
      case Days => Double.NaN
      case Weeks => Double.NaN
      case Months => length / 12.0
      case Years => length
    }

  val months: Double =
    unitsMatching{
      case Days => Double.NaN
      case Weeks => Double.NaN
      case Months => length
      case Years => length * 12.0
    }


  val weeks: Double =
    unitsMatching {
      case Days => length / 7.0
      case Weeks => length
      case Months => Double.NaN
      case Years => Double.NaN
    }

  val days: Double =
    unitsMatching {
      case Days => length
      case Weeks => length * 7.0
      case Months => Double.NaN
      case Years => Double.NaN
    }

}

