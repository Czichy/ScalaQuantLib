package org.scalaquant.core.common.time

import org.joda.time.DateTimeConstants._

object DayOfWeek extends Enumeration(1) {
  type DayOfWeek = Value

  val Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday = Value

  implicit class WeekdayWrapper(val dayOfWeek: DayOfWeek) extends AnyVal{
    def asLongWeekDay: String = {
      dayOfWeek.id match {
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
    def asShortWeekDay: String = {
      dayOfWeek.id match {
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
    def asShortestWeekDay: String = {
      dayOfWeek.id match {
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