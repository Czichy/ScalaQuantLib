package org.scalaquant.core.indexes

import java.util.Calendar

import org.joda.time.LocalDate

/**
 * Created by neo on 2015-03-02.
 */
trait Index {
  def name: String
  def fixingCalendar: Calendar
  def isValidFixingDate(date: LocalDate): Boolean
}
