package org.scalaquant.core.cashflows

import org.joda.time.LocalDate
import org.scalaquant.core.common.{ Event, Settings }

/**
 * Created by neo on 2015-03-01.
 */

trait CashFlow extends Event {
  def amount: Double

  override def hasOccurred(refDate: Option[LocalDate], includeRefDate: Boolean = true) = {
    refDate.map {
      this.date.isBefore(_)
    } getOrElse {
      super.hasOccurred(refDate)
    }
  }

  def tradingExCoupon(refDate: Option[LocalDate]): Boolean = {
    val actualRefDate = refDate.getOrElse(Settings.evaluationDate)
    this.exCouponDate.isBefore(actualRefDate) || this.exCouponDate.isEqual(actualRefDate)
  }

  def exCouponDate: LocalDate

  def isBefore(other: CashFlow): Boolean = this.isBefore(other)
}

object CashFlow {

  type Leg = List[CashFlow]

  implicit class CashFlowOperation(val cf: CashFlow) extends AnyVal {
    def <(other: CashFlow) = {
      cf.date.isBefore(other.date)
    }
  }
}