package org.scalaquant.common.time.daycounts

import org.joda.time.DateTimeConstants._
import org.joda.time.{Days, LocalDate}

import org.scalaquant.common.time.JodaDateTimeHelper._
import org.scalaquant.core.types._

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

import scala.annotation.tailrec

//! Actual/Actual day count
/*! The day count can be calculated according to:

    - the ISDA convention, also known as "Actual/Actual (Historical)",
      "Actual/Actual", "Act/Act", and according to ISDA also "Actual/365",
      "Act/365", and "A/365";
    - the ISMA and US Treasury convention, also known as
      "Actual/Actual (Bond)";
    - the AFB convention, also known as "Actual/Actual (Euro)".

    For more details, refer to
    http://www.isda.org/publications/pdf/Day-Count-Fracation1999.pdf

    \test the correctness of the results is checked against known good values.
*/

object ActualActual {

  sealed abstract class Convention
  case object ICMA extends Convention
  case object Bond extends Convention
  case object ISDA extends Convention
  case object Historical extends Convention
  case object Actual365 extends Convention
  case object AFB extends Convention
  case object Euro extends Convention

  private val ICMAImplement = new DayCountConvention {
    val name: String = "Actual/Actual (ICMA)"

    def fractionOfYear(date1: LocalDate,
                       date2: LocalDate,
                       refDate1: Option[LocalDate] = None,
                       refDate2: Option[LocalDate] = None): YearFraction = {

      date1 compareTo date2 match {
        case 0 => 0.0
        case 1 => -fractionOfYear(date2, date1, refDate1, refDate2)
        case -1  =>
          val refPeriodStart = refDate1 getOrElse date1
          val refPeriodEnd = refDate2 getOrElse date2

          require(refPeriodEnd > refPeriodStart && refPeriodEnd > date1,
              s"invalid reference period: date 1: $date1, date 2: $date2" +
              s", reference period start: $refPeriodStart, reference period end: $refPeriodEnd")

          val range = Days.daysBetween(refPeriodStart, refPeriodEnd).getDays
          val (months, refStart, refEnd) = (0.5 + 12 * range.toDouble / 365).toInt match {
            case 0 => (12, date1, date1.plusYears(1))
            case i => (i, refPeriodStart, refPeriodEnd)
          }

          val period = months / 12.0

          if (date2 <= refEnd) {
            // here refPeriodEnd is a future (notional?) payment date
            if (date1 >= refStart) {
              // here refPeriodStart is the last (maybe notional)
              // payment date.
              // refPeriodStart <= d1 <= d2 <= refPeriodEnd
              // [maybe the equality should be enforced, since
              // refPeriodStart < d1 <= d2 < refPeriodEnd
              // could give wrong results] ???
              period * dayCount(date1,date2) / dayCount(refPeriodStart,refPeriodEnd)
            } else {
              // here refPeriodStart is the next (maybe notional)
              // payment date and refPeriodEnd is the second next
              // (maybe notional) payment date.
              // d1 < refPeriodStart < refPeriodEnd
              // AND d2 <= refPeriodEnd
              // this case is long first coupon

              // the last notional payment date
              val previousRef = refStart.plusMonths(-months)
              if (date2 > refStart)
                fractionOfYear(date1, refStart, Some(previousRef), Some(refStart)) +
                  fractionOfYear(refStart, date2, Some(refStart), Some(refEnd))
              else
                fractionOfYear(date1,date2,Some(previousRef),Some(refStart))
            }
          } else {
            // here refPeriodEnd is the last (notional?) payment date
            // d1 < refPeriodEnd < d2 AND refPeriodStart < refPeriodEnd
            require(refStart <= date1, "invalid dates: date1 < refPeriodStart < refPeriodEnd < date2");
            // now it is: refPeriodStart <= d1 < refPeriodEnd < d2

            // the part from d1 to refPeriodEnd
            val date1ToRefEnd = fractionOfYear(date1, refEnd, Some(refStart), Some(refEnd))

            // the part from refPeriodEnd to d2
            // count how many regular periods are in [refPeriodEnd, d2],
            // then add the remaining time

            @tailrec
            def sumUp(i:Int, sum: Double): (Double, LocalDate, LocalDate) = {
              val newRefStart = refEnd.plusMonths(months*i)
              val newRefEnd = refEnd.plusMonths(months*(i+1))
              if (date2 < newRefEnd) (sum, newRefStart, newRefEnd) else sumUp(i+1, sum+period)
            }
            val (remaining, newRefStart, newRefEnd) = sumUp(0, date1ToRefEnd)

            remaining + fractionOfYear(newRefStart, date2, Some(newRefStart), Some(newRefEnd))
          }
      }
    }
  }

  private val ISDAImplement = new DayCountConvention {
    val name: String = "Actual/Actual (ISDA)"

    private def base(date: LocalDate) = if (date.inLeapYear) 366.0 else 365.0

    override def fractionOfYear(date1: LocalDate,
                                date2: LocalDate,
                                refDate1: Option[LocalDate] = None,
                                refDate2: Option[LocalDate] = None): YearFraction = {
      date1 compareTo date2 match {
        case 0 => 0.0
        case 1 => -fractionOfYear(date2, date1, None, None)
        case -1  =>
          val years = Days.daysBetween(date1, date2).toPeriod.getYears

          val fractionOfYearStart = dayCount(date1, new LocalDate(date1.getYear + 1, JANUARY, 1)) / base(date1)
          val fractionOfYearEnd = dayCount(new LocalDate(date2.getYear, JANUARY, 1), date2) / base(date2)

          years.toDouble + fractionOfYearStart + fractionOfYearEnd
      }
    }
  }

  private val AFBImplement = new DayCountConvention {
    val name: String = "Actual/Actual (AFB)"

    override def fractionOfYear(date1: LocalDate,
                                date2: LocalDate,
                                refDate1: Option[LocalDate] = None,
                                refDate2: Option[LocalDate] = None): YearFraction = {
      date1 compareTo date2 match {
        case 0 => 0.0
        case 1 => fractionOfYear(date2, date1, refDate1, refDate2)
        case -1 =>
          val (yy1, mm1, dd1) = date1.YMD
          val (yy2, mm2, dd2) = date2.YMD

          val range = Days.daysBetween(date1, date2)
          val completeYears = range.toPeriod.getYears
          val febInStart = new LocalDate(yy1, FEBRUARY, 29) >= date1

          if (completeYears > 1) {
            val fractionOfYearEndDate = new LocalDate(yy2 - completeYears, mm2, dd2)
            val yearBase = if (febInStart) 366 else 365

            completeYears.toDouble + Days.daysBetween(date1, fractionOfYearEndDate).getDays.toDouble / yearBase
          } else {
            val febInEnd = new LocalDate(yy2, FEBRUARY, 29) < date2
            val yearBase = if (febInStart || febInEnd) 366 else 365

            range.getDays.toDouble / yearBase
          }
      }
    }
  }

  def apply(convention: Convention = ISDA): DayCountConvention = {
    convention match {
      case ICMA | Bond => ICMAImplement
      case ISDA | Historical | Actual365 => ISDAImplement
      case AFB | Euro => AFBImplement
    }
  }
}

