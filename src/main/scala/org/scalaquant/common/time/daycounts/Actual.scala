package org.scalaquant.common.time.daycounts

import org.joda.time.DateTimeConstants._
import org.joda.time.{ Days, LocalDate }
import org.scalaquant.common.time.Frequency._

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._


object Actual365Fixed {

  private val actual365Impl = new DayCountConvention {
    val name: String = "Actual/365 (Fixed)"
    def fractionOfYear(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency): Double = dayCount(date1, date2).toDouble / 365.0
  }

  def apply(): DayCountConvention = actual365Impl
}

object Actual364 {

  private val actual364Impl = new DayCountConvention {
     val name: String = "Actual/364 "
     def fractionOfYear(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency): Double = dayCount(date1, date2).toDouble / 364.0
     def fractionOfYear(date1: LocalDate, date2: LocalDate, refDate1: LocalDate, refDate2: LocalDate): Double = dayCount(date1, date2).toDouble / 364.0

  }

  def apply(): DayCountConvention = actual364Impl
}

object Actual360 {

  private val actual360Impl =  new DayCountConvention {
    val name: String = "Actual/360 "
    override def fractionOfYear(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency): Double = dayCount(date1, date2).toDouble / 360.0
  }

  def apply(): DayCountConvention = actual360Impl
}

object Actual365NoLeap {

  private def S(date: LocalDate) = {
    val s = date.getDayOfMonth + MonthOffset(date.getMonthOfYear - 1) + (date.getYear * 365)
    if (date.getMonthOfYear == FEBRUARY && date.getDayOfMonth == 29) s - 1 else s
  }

  private val actual365NoLeapImplt = new DayCountConvention {
    val name: String = "Actual/365 (NL)"
    override def dayCount(date1: LocalDate, date2: LocalDate): Int = S(date2) - S(date1)
    override def fractionOfYear(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency): Double = dayCount(date1, date2).toDouble / 365.0

  }
  def apply(): DayCountConvention = actual365NoLeapImplt
}

object OneDay {

  private val oneImpl = new DayCountConvention {
    val name: String = "1/1"
    override def dayCount(date1: LocalDate, date2: LocalDate): Int = if (date1 > date2) -1 else 1
    override def fractionOfYear(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency): Double = dayCount(date1, date2).toDouble
  }

  def apply(): DayCountConvention = oneImpl
}

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

    override def fractionOfYear(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency): Double = {
      require(date2 <= date3, "Date 3 is The coupon payment date following Date2")

      date1 compareTo date2 match {
        case 0 => 0.0
        case 1 => -fractionOfYear(date2, date1, date3, freq)
        case -1  => dayCount(date1,date2) / (freq.value * dayCount(date1,date3))
      }
    }
  }

  private val ISDAImplement = new DayCountConvention {
    val name: String = "Actual/Actual (ISDA)"

    private def base(date: LocalDate) = if (date.inLeapYear) 366.0 else 365.0

    override def fractionOfYear(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency): Double = {
      date1 compareTo date2 match {
        case 0 => 0.0
        case 1 => fractionOfYear(date2, date1, date3, freq)
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

    override def fractionOfYear(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency): Double = {
      date1 compareTo date2 match {
        case 0 => 0.0
        case 1 => fractionOfYear(date2, date1, date3, freq)
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

