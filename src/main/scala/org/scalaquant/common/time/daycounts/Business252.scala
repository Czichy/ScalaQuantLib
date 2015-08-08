package org.scalaquant.common.time.daycounts

import org.joda.time.LocalDate
import org.scalaquant.common.time.calendars.{Brazil, BusinessCalendar}
import org.scalaquant.core.types.YearFraction

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

sealed class Business252(calendar: BusinessCalendar)  extends DayCountConvention {
  import Business252._
  override def name: String = s"Business/252(${calendar.name})"

  //TODO: Add caching API
  override def dayCount(d1: LocalDate, d2: LocalDate) = {
    if (sameMonthYear(d1,d2) || d1 >= d2) {
      // we treat the case of d1 > d2 here, since we'd need a
      // second cache to get it right (our cached figures are
      // for first included, last excluded and might have to be
      // changed going the other way.)
      calendar.businessDaysBetween(d1, d2)
    } else if (sameYear(d1, d2)) {
      0
//      Cache& cache = monthlyFigures_[calendar_.name()];
//      BigInteger total = 0;
//      Date d;
//      // first, we get to the beginning of next month.
//      d = Date(1,d1.month(),d1.year()) + 1*Months;
//      total += calendar_.businessDaysBetween(d1, d);
//      // then, we add any whole months (whose figures might be
//      // cached already) in the middle of our period.
//      while (!sameMonthYear(d,d2)) {
//        total += businessDays(cache, calendar_,
//          d.month(), d.year());
//        d += 1*Months;
//      }
//      // finally, we get to the end of the period.
//      total += calendar_.businessDaysBetween(d, d2);
//      return total;
    } else {
      //Cache& cache = monthlyFigures_[calendar_.name()];
//      OuterCache& outerCache = yearlyFigures_[calendar_.name()];
//      BigInteger total = 0;
//      Date d;
//      // first, we get to the beginning of next year.
//      // The first bit gets us to the end of this month...
//      d = Date(1,d1.month(),d1.year()) + 1*Months;
//      total += calendar_.businessDaysBetween(d1, d);
//      // ...then we add any remaining months, possibly cached
//      for (Integer m = Integer(d1.month())+1; m <= 12; ++m) {
//        total += businessDays(cache, calendar_,
//          Month(m), d.year());
//      }
//      // then, we add any whole year in the middle of our period.
//      d = Date(1,January,d1.year()+1);
//      while (!sameYear(d,d2)) {
//        total += businessDays(outerCache, cache,
//          calendar_, d.year());
//        d += 1*Years;
//      }
//      // finally, we get to the end of the period.
//      // First, we add whole months...
//      for (Integer m = 1; m<Integer(d2.month()); ++m) {
//        total += businessDays(cache, calendar_,
//          Month(m), d2.year());
//      }
//      // ...then the last bit.
//      d = Date(1,d2.month(),d2.year());
//      total += calendar_.businessDaysBetween(d, d2);
//      return total;
      0
    }
  }
  def fractionOfYear(date1: LocalDate,
                     date2: LocalDate,
                     refDate1: Option[LocalDate] = None,
                     refDate2: Option[LocalDate] = None): YearFraction = dayCount(date1, date2)/252.0

}

object Business252 {
  private type CheckSame = (LocalDate, LocalDate) => Boolean
  private val sameYear: CheckSame = _.getYear == _.getYear
  private val sameMonth: CheckSame = _.getMonthOfYear == _.getMonthOfYear
  private val sameMonthYear: CheckSame = (date1, date2) => List(sameYear, sameMonth).map(_(date1,date2)).reduce(_ && _)

  def apply(calendar: BusinessCalendar = Brazil()): DayCountConvention = new Business252(calendar)
}
