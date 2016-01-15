package org.scalaquant.core.common.time


import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.joda.time.LocalDate

import org.scalaquant.math.Comparing.ImplicitsOps._
import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.core.common.time.JodaDateTimeHelper._
import org.scalaquant.core.common.time.Period.PeriodOrder

/**
  * Created by neo on 12/14/15.
  */

object PaymentSchedule{

  def nextTwentieth(date: LocalDate, rule: DateGeneration.Rule): LocalDate = {
    val temp = LocalDate.now.withYear(date.getYear).withMonthOfYear(date.getMonthOfYear).withDayOfMonth(20)
    val next = if (temp < date) temp + Period(1, TimeUnit.Months) else temp

    if (rule == DateGeneration.TwentiethIMM || rule == DateGeneration.OldCDS || rule == DateGeneration.CDS) {
      val month = next.getMonthOfYear
      if (month % 3 != 0) next + Period(3 - month % 3, TimeUnit.Months) // not a main IMM nmonth
      else next
    } else {
      next
    }

  }

  def previousTwentieth(date: LocalDate, rule: DateGeneration.Rule): LocalDate = {
    val temp = LocalDate.now.withYear(date.getYear).withMonthOfYear(date.getMonthOfYear).withDayOfMonth(20)
    val prev = if (temp > date) temp - Period(1, TimeUnit.Months) else temp

    if (rule == DateGeneration.TwentiethIMM || rule == DateGeneration.OldCDS || rule == DateGeneration.CDS) {
      val month = prev.getMonthOfYear
      if (month % 3 != 0) prev - Period(month % 3, TimeUnit.Months) // not a main IMM nmonth
      else prev
    } else {
      prev
    }

  }


}
case class PaymentSchedule(from: Option[LocalDate],
                           to: Option[LocalDate],
                           tenor :Period,
                           calendar: BusinessCalendar,
                           convention: BusinessDayConvention,
                           terminationDateConvention: BusinessDayConvention,
                           rule: DateGeneration.Rule,
                           endOfMonth: Boolean,
                           first: LocalDate,
                           nextToLast: LocalDate) {


  private val isEndOfMonth = if (Period(1, TimeUnit.Months) > tenor) false else endOfMonth

  private val firstDate = from.filter(_ == first)
  private val nextToLastDate = to.filter(_ == nextToLast)

  require(to.isDefined, "null termination date")
  require(tenor.length > 0 || (tenor.length == 0 && rule == DateGeneration.Zero), s"non positive tenor ($tenor) not allowed")
  require(from < to, "from date (" << effectiveDate << ") later than or equal to termination date (" << terminationDate << ")");

  rule match {
    case DateGeneration.Backward =>
    case DateGeneration.Forward =>
    case DateGeneration.ThirdWednesday =>
      require(IMM.isIMMdate(firstDate.get, false), s"first date ($firstDate) is not an IMM date")
    case DateGeneration.Zero =>
    case DateGeneration.Twentieth =>
    case DateGeneration.TwentiethIMM =>
    case DateGeneration.OldCDS =>
    case DateGeneration.CDS =>
      require(true, "first date incompatible with $rule date generation rule")
  }

  def previousDate(refDate: LocalDate)
  def nextDate(refDate: LocalDate)

  //def
  //const std::vector<bool>& isRegular() const;


  //! truncated schedule
  def until(truncationDate: LocalDate)
}
