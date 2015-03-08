package org.scalaquant.core.common.time

import org.joda.time.LocalDate
import org.joda.time.DateTimeConstants._

/**
 * Created by neo on 2015-03-08.
 */

object WeekDay{

  implicit class WeekdayWrapper(val date: LocalDate) extends AnyVal{
    def asLongWeekDay: String = {
      date.getDayOfWeek match {
        case SUNDAY => "Sunday"
        case MONDAY => "Monday"
        case TUESDAY => "Tuesday"
        case WEDNESDAY => "Wednesday"
        case THURSDAY => "Thursday"
        case FRIDAY => "Friday"
        case SATURDAY => "Saturday"
        case _ => ""
      }
    }
    def asShortWeekDay:String = {
      date.getDayOfWeek match {
        case SUNDAY => "Sun"
        case MONDAY => "Mon"
        case TUESDAY => "Tue"
        case WEDNESDAY => "Wed"
        case THURSDAY => "Thu"
        case FRIDAY => "Fri"
        case SATURDAY => "Sat"
        case _ => ""
      }
    }
    def asShortestWeekDay:String = {
      date.getDayOfWeek match {
        case SUNDAY => "Su"
        case MONDAY => "Mo"
        case TUESDAY => "Tu"
        case WEDNESDAY => "We"
        case THURSDAY => "Th"
        case FRIDAY => "Fr"
        case SATURDAY => "Sa"
        case _ => ""
      }
    }
    def asString: String = asLongWeekDay
  }

}