package org.scalaquant.core.common

import org.joda.time.LocalDate

/**
 * Created by neo on 2015-03-01.
 */
object Settings {
  private var evaluationDate_ = LocalDate.now
  //All the val values here should read from config file and readonly in system
  private val includeReferenceDateEvents_ = false
  private val enforcesTodaysHistoricFixings_ = false
  private val includeTodaysCashFlows_ = None
  def evaluationDate: LocalDate = evaluationDate_
  def anchorEvaluationDate(): Unit = {
    if (!evaluationDate_.equals(LocalDate.now)) {
      resetEvaluationDate
    }
  }
  def resetEvaluationDate(): Unit = {
    evaluationDate_ = LocalDate.now
  }
  def includeReferenceDateEvents: Boolean = includeReferenceDateEvents_
  def includeTodaysCashFlows: Option[Boolean] = includeTodaysCashFlows_
  def enforcesTodaysHistoricFixings: Boolean = enforcesTodaysHistoricFixings_
}
