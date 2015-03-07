package org.scalaquant.core.common.time.daycounts

import org.joda.time.DateTimeConstants._
import org.joda.time.{ Days, LocalDate }

import scala.language.implicitConversions
import org.scalaquant.core.common.time.JodaDateTimeHelper._

/**
 * Created by neo on 2015-03-02.
 */

object Actual365Fixed extends DayCountConvention {
  override def name: String = "Actual/365 (Fixed)"
  override def fraction(date1: LocalDate, date2: LocalDate): Double = {
    dayCount(date1, date2).toDouble / 365.0
  }
}

object Actual364 extends DayCountConvention {
  override def name: String = "Actual/364 "
  override def fraction(date1: LocalDate, date2: LocalDate): Double = {
    dayCount(date1, date2).toDouble / 364.0
  }
}

object Actual360 extends DayCountConvention {
  override def name: String = "Actual/360 "
  override def fraction(date1: LocalDate, date2: LocalDate): Double = {
    dayCount(date1, date2).toDouble / 360.0
  }
}

object Actual365NoLeap extends DayCountConvention {

  private def S(date: LocalDate) = {
    val s = date.getDayOfMonth + MonthOffset(date.getMonthOfYear - 1) + (date.getYear * 365)
    if (date.getMonthOfYear == FEBRUARY && date.getDayOfMonth == 29) s - 1 else s
  }
  override def name: String = "Actual/365 (NL)"
  override def dayCount(date1: LocalDate, date2: LocalDate): Int = S(date2) - S(date1)
  override def fraction(date1: LocalDate, date2: LocalDate): Double = {
    dayCount(date1, date2).toDouble / 365.0
  }
}

object OneDay extends DayCountConvention {
  override def name: String = "1/1"
  override def dayCount(date1: LocalDate, date2: LocalDate): Int = if (date1 > date2) -1 else 1
  override def fraction(date1: LocalDate, date2: LocalDate): Double = {
    dayCount(date1, date2).toDouble
  }
}

object ActualActual {

  trait Convention

  object Convention {

    case object ICMA extends Convention

    case object Bond extends Convention

    case object ISDA extends Convention

    case object Historical extends Convention

    case object Actual365 extends Convention

    case object AFB extends Convention

    case object Euro extends Convention

  }

  private val ICMAImplement = new DayCountConvention {
    override def name: String = "Actual/Actual (ICMA)"

    override def fraction(date1: LocalDate, date2: LocalDate) = fraction(date1, date2, date2, DayCountConvention.Frequency.Annual)

    override def fraction(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: DayCountConvention.Frequency): Double = {
      (date1, date2) match {
        case (date1, date2) if (date1 == date2) => 0.0
        case (date1, date2) if (date1 > date2) => fraction(date2, date1, date3, freq)
        case (date1, date2) => dayCount(date1, date2) / (freq.value * dayCount(date1, date3))
      }
    }
  }
  private val ISDAImplement = new DayCountConvention {
    override def name: String = "Actual/Actual (ISDA)"

    override def fraction(date1: LocalDate, date2: LocalDate): Double = {
      (date1, date2) match {
        case (date1, date2) if (date1 == date2) => 0.0
        case (date1, date2) if (date1 > date2) => fraction(date2, date1)
        case (date1, date2) =>
          val years = Days.daysBetween(date1, date2).toPeriod.getYears

          val date1Base = if (date1.inLeapYear) 366.0 else 365.0
          val date2Base = if (date2.inLeapYear) 366.0 else 365.0

          val fractionStart = dayCount(date1, new LocalDate(date1.getYear + 1, JANUARY, 1)) / date1Base
          val fractionEnd = dayCount(new LocalDate(date2.getYear, JANUARY, 1), date2) / date2Base

          years.toDouble + fractionStart + fractionEnd
      }
    }
  }
  private val AFBImplement = new DayCountConvention {
    override def name: String = "Actual/Actual (AFB)"

    override def fraction(date1: LocalDate, date2: LocalDate): Double = {
      (date1, date2) match {
        case (date1, date2) if (date1 == date2) => 0.0
        case (date1, date2) if (date1 > date2) => fraction(date2, date1)
        case (date1, date2) =>
          val (yy1, mm1, dd1) = date1.YMD
          val (yy2, mm2, dd2) = date2.YMD

          val range = Days.daysBetween(date1, date2)
          val completeYears = range.toPeriod.getYears
          val febInStart = new LocalDate(yy1, FEBRUARY, 29) >= date1

          if (completeYears > 1) {
            val fractionEndDate = new LocalDate(yy2 - completeYears, mm2, dd2)
            val yearBase = if (febInStart) 366 else 365

            completeYears.toDouble + Days.daysBetween(date1, fractionEndDate).getDays.toDouble / yearBase
          } else {
            val febInEnd = new LocalDate(yy2, FEBRUARY, 29) < date2
            val yearBase = if (febInStart || febInEnd) 366 else 365

            range.getDays.toDouble / yearBase
          }
      }
    }
  }

  def apply(convention: Convention = Convention.ISDA): DayCountConvention = {
    convention match {
      case Convention.ICMA | Convention.Bond => ICMAImplement
      case Convention.ISDA | Convention.Historical | Convention.Actual365 => ISDAImplement
      case Convention.AFB | Convention.Euro => AFBImplement
    }
  }
}

