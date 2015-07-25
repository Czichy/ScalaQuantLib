package org.scalaquant.common.time

import java.util.concurrent.ConcurrentSkipListSet

import org.joda.time.LocalDate
import org.scalaquant.common.Settings

/** European Central Bank reserve maintenance dates */
object ECB {

  private val knownDates = new ConcurrentSkipListSet[LocalDate]()

  def isECBdate(date: LocalDate): Boolean = ???

  def isECBcode(code: String): Boolean = ???

  def addDate(date: LocalDate): Unit = ???

  def removeDate(date: LocalDate): Unit = ???

  def nextDate(code: String): LocalDate = ???

  def nextDates(code: String): List[LocalDate] = ???

  def nextDate(date: LocalDate): LocalDate =  ???

  def nextDates(date: LocalDate): List[LocalDate] =  ???

  def code(date: LocalDate): String =  ???
  def date(code: String): LocalDate = ???

  def nextCode(date: LocalDate = Settings.evaluationDate): String = ???
  def nextCode(code: String): String = ???
}
