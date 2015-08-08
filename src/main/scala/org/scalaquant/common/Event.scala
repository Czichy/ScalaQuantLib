package org.scalaquant.common

import org.joda.time.LocalDate

import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._


abstract class Event {

  def date: LocalDate

  //! returns true if an event has already occurred before a date
  /*! If includeRefDate is true, then an event has not occurred if its
      date is the same as the refDate, i.e. this method returns false if
      the event date is the same as the refDate.
  */
  def hasOccurred(refDate: LocalDate, includeRefDate: Boolean = true): Boolean = {
    if (includeRefDate) date < refDate else date <= refDate
  }
}