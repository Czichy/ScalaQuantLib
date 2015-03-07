package org.scalaquant.core.common

import org.joda.time.LocalDate

/**
 * Created by neo on 2015-02-28.
 */

import scala.language.implicitConversions
import org.scalaquant.core.common.time.JodaDateTimeHelper._

trait Event {
  def date: LocalDate

  //! returns true if an event has already occurred before a date
  /*! If includeRefDate is true, then an event has not occurred if its
      date is the same as the refDate, i.e. this method returns false if
      the event date is the same as the refDate.
  */
  def hasOccurred(refDate: Option[LocalDate], includeRefDate: Boolean = true): Boolean = {
    val actualRefDate = refDate.getOrElse(Settings.evaluationDate)
    if (includeRefDate) date < actualRefDate else date <= actualRefDate
  }
}

case class SimpleEvent(date: LocalDate) extends Event