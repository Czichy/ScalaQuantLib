package org.scalaquant.core.common.time

import org.joda.time.LocalDate
import org.joda.time.DateTimeConstants._
import org.scalaquant.core.common.Settings

import scala.language.implicitConversions
import org.scalaquant.core.common.time.JodaDateTimeHelper._


/** Main cycle of the International %Money Market (a.k.a. %IMM) months */
object IMM {

  private val MonthCodes = "FGHJKMNQUVXZ"
  private val Months = MonthCodes.toList.map(_.toString)
  private val codeRegex = s"""([$MonthCodes])(\d)""".r
  private val mainCycleRegex =  """([HMZU])(\d)""".r

  def isIMMdate(date: LocalDate, mainCycle: Boolean): Boolean = {
    date.getDayOfWeek match {
      case WEDNESDAY =>
        date.getDayOfMonth match {
          case d if d < 15 || d > 21 => false
          case _ =>
            if (!mainCycle)
              true
            else
              date.getMonthOfYear match {
                case MARCH | JUNE | SEPTEMBER | DECEMBER => true
                case _ => false
              }
        }
      case _ => false
    }
  }

  def isIMMCode(code: String, mainCycle: Boolean): Boolean = {
    val matching = if (mainCycle) mainCycleRegex else codeRegex
    code.toUpperCase.matches(matching.regex)
  }

  def code(date: LocalDate): String = {
    if (!isIMMdate(date, mainCycle = false)) "" else Months(date.getMonthOfYear - 1) + (date.getYear % 10)
  }

  def date(immCode: String, refDate: LocalDate = Settings.evaluationDate): Option[LocalDate] = {
    if (isIMMCode(immCode, mainCycle = false))
      immCode.toUpperCase match {
        case codeRegex(c, y) =>
          val month = Months.indexOf(c) + 1
          val refYear = refDate.getYear
          val year = refYear + (if (y.toInt == 0 && refYear <= 1909) y + 10 else y).toInt - (refYear % 10)
          val result = nextDate(new LocalDate(year, month, 1), mainCycle = false)
          Some(if (result < refDate) nextDate(new LocalDate(year + 10, month, 1), mainCycle = false) else result)
        case _ => None
      }
    else None
  }


  def nextDate(date: LocalDate = Settings.evaluationDate, mainCycle: Boolean = true): LocalDate = {
    val (refYear, refYearMonth, refDay) = date.YMD

    val offset = if (mainCycle) 3 else 1
    val skipping = offset - (refYearMonth % offset)

    val (year, month) = if (skipping != offset || refDay > 21) {
      val nextCycleMonth = refYearMonth + skipping
      if (nextCycleMonth <= 12) (refYear, nextCycleMonth) else (refYear + 1, nextCycleMonth - 11)
    } else {
      (refYear, refYearMonth)
    }

    val result = nthWeekday(3, DayOfWeek.Wednesday, month, year)
    if (result <= date) nextDate(new LocalDate(year, month, 22), mainCycle) else result
  }

  def nextDate(immCode: String, mainCycle: Boolean, refDate: LocalDate): Option[LocalDate] = {
    date(immCode, refDate).map{ immDate => nextDate(immDate.plusDays(1), mainCycle) }
  }

  def nextCode(date: LocalDate, mainCycle: Boolean): String = {
    code(nextDate(date, mainCycle))
  }

  def nextCode(immCode: String, mainCycle: Boolean, refDate: LocalDate): Option[String] = {
    nextDate(immCode, mainCycle, refDate).map(code)
  }


}
