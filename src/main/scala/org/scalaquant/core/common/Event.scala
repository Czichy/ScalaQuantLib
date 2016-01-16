package org.scalaquant.core.common

import java.time.LocalDate
import org.scalaquant.math.Comparing.Implicits._
import org.scalaquant.math.Comparing.ImplicitsOps._

object Event{

  def hasOccurred(eventDate: LocalDate, refDate: LocalDate, includeRefDate: Boolean = true): Boolean = {
    if (includeRefDate) eventDate < refDate else eventDate <= refDate
  }

}