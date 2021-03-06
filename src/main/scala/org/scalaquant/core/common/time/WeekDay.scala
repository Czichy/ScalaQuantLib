package org.scalaquant.core.common.time

import java.time.DateTimeConstants._
/*
 * Joda time and this library use the ISO definitions, where 1 is Monday and 7 is Sunday.
 */
object DayOfWeek extends Enumeration(1) {
  type DayOfWeek = Value

  val Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday = Value

  implicit class WeekdayWrapper(val dayOfWeek: DayOfWeek) extends AnyVal{

    def >=(other: DayOfWeek) = dayOfWeek.id >= other.id
    def >=(other: Int) = dayOfWeek.id >= other


    def -(other: Int) = dayOfWeek.id - other
    def +(other: Int) = dayOfWeek.id + other
    def asLongWeekDay: String = dayOfWeek.id match {
        case SUNDAY => "Sunday"
        case MONDAY => "Monday"
        case TUESDAY => "Tuesday"
        case WEDNESDAY => "Wednesday"
        case THURSDAY => "Thursday"
        case FRIDAY => "Friday"
        case SATURDAY => "Saturday"
      }

    def asShortWeekDay: String = dayOfWeek.id match {
        case SUNDAY => "Sun"
        case MONDAY => "Mon"
        case TUESDAY => "Tue"
        case WEDNESDAY => "Wed"
        case THURSDAY => "Thu"
        case FRIDAY => "Fri"
        case SATURDAY => "Sat"
      }

    def asShortestWeekDay: String = dayOfWeek.id match {
        case SUNDAY => "Su"
        case MONDAY => "Mo"
        case TUESDAY => "Tu"
        case WEDNESDAY => "We"
        case THURSDAY => "Th"
        case FRIDAY => "Fr"
        case SATURDAY => "Sa"
      }

    override def toString: String = asLongWeekDay
  }

}