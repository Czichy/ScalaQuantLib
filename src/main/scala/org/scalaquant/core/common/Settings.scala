package org.scalaquant.core.common

import org.joda.time.LocalDate

object Settings {
  def evaluationDate: LocalDate = LocalDate.now
  // always use the current date as the default evaluation date

  //All the val values here should read from config file and readonly in system
  private val includeReferenceDateEvents_ = false
  private val enforcesTodaysHistoricFixings_ = false
  private val includeTodaysCashFlows_ = None

  def includeReferenceDateEvents: Boolean = includeReferenceDateEvents_
  def includeTodaysCashFlows: Option[Boolean] = includeTodaysCashFlows_
  def enforcesTodaysHistoricFixings: Boolean = enforcesTodaysHistoricFixings_
}
