package org.scalaquant.core.common.time

import org.scalaquant.core.common.time.TimeUnit._

/**
 * Created by neo on 2015-03-07.
 */

case class Period(length: Int = 0, units: TimeUnit = Days)

object Period {
  val Empty = Period()
  val Unknown = Period(999)
  import Frequency._
  def apply(freq: Frequency): Period = {
    freq match {
      case NoFrequency => Empty
      case Once => Period(0, Years)
      case Annual => Period(1, Years)
      case Semiannual | EveryFourthMonth | Quarterly | Bimonthly | Monthly => Period( 12 / freq.value, Months )
      case EveryFourthWeek | Biweekly | Weekly => Period( 52 / freq.value, Weeks )
      case Daily => Period(1)
      case _ => Unknown
    }
  }

  implicit class PeriodOperation(val period: Period) extends AnyVal {
    def unary_- = Period(-period.length,period.units)
    def *(n: Int) = Period(n * period.length, period.units)
    def *:(n: Int) = *(n)
//    def *(other: Period) =
//    def /(other: Period) =
//    def >(other: Period) =
//    def <(other: Period) =

    def shortDescription: String = {
      val n = period.length
      period.units match {
        case Days =>
          val week = n / 7 match {
            case 0 => ""
            case x => x+"W"
          }
          val day = n % 7 match {
            case 0 => ""
            case x => x+"D"
          }
          week + day
        case Weeks => n+"W"
        case Months =>
          val year = n / 12 match {
            case 0 => ""
            case x => x+"Y"
          }
          val month = n % 12 match {
            case 0 => ""
            case x => x+"M"
          }
          year + month
        case Years => n+"Y"
      }
    }
    def longDescription: String = {
      val n = period.length
      period.units match {
        case Days =>
          val week = n / 7 match {
            case 0 => ""
            case 1 => "1 week "
            case x => x+" weeks "
          }
          val day = n % 7 match {
            case 0 => ""
            case 1 => "1 day"
            case x => x+" days"
          }
          (week + day).trim
        case Weeks => if (n == 1) s"1 week" else n+" weeks"
        case Months =>
          val year = n / 12 match {
            case 0 => ""
            case 1 => "1 year "
            case x => x+" years "
          }
          val month = n % 12 match {
            case 0 => ""
            case 1 => "1 month"
            case x => x+" months"
          }
          (year + month).trim
        case Years => if (n == 1) "1 year" else n+" years"
      }
    }
    def frequency: Frequency = {
      val length = Math.abs(period.length)

      if (length == 0) {
        if (period.units == Years) Once else NoFrequency
      }else {
        period.units match {
          case Years => if (length == 1) Annual else OtherFrequency
          case Months =>
            length match {
              case 6 => Semiannual
              case 4 => EveryFourthMonth
              case 3 => Quarterly
              case 2 => Bimonthly
              case 1 => Monthly
              case _ => OtherFrequency
            }
          case Weeks =>
            length match {
              case 1 => Weekly
              case 2 => Biweekly
              case 4 => EveryFourthWeek
              case _ => OtherFrequency
            }
          case Days => if (length == 1) Daily else OtherFrequency
          case _ => OtherFrequency
        }
      }
    }

    def years: Double = {
      if (period.length == 0 ) {
        0.0
      } else {
        period.units match {
          case Days => Double.NaN
          case Weeks => Double.NaN
          case Months => period.length / 12.0
          case Years => period.length
        }
      }
    }
    def months: Double = {
      if (period.length == 0 ) {
        0.0
      } else {
        period.units match {
          case Days => Double.NaN
          case Weeks => Double.NaN
          case Months => period.length
          case Years => period.length * 12.0
        }
      }
    }
    def weeks: Double = {
      if (period.length == 0 ) {
        0.0
      } else {
        period.units match {
          case Days => period.length / 7.0
          case Weeks => period.length
          case Months => Double.NaN
          case Years => Double.NaN
        }
      }
    }
    def days: Double ={
      if (period.length == 0 ) {
        0.0
      } else {
        period.units match {
          case Days => period.length
          case Weeks => period.length * 7.0
          case Months => Double.NaN
          case Years => Double.NaN
        }
      }
    }
  }
}