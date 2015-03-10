package org.scalaquant.core.cashflows

import org.joda.time.LocalDate
import scala.language.implicitConversions
import org.scalaquant.core.common.time.JodaDateTimeHelper._
import org.scalaquant.core.common.{ Event, Settings }

/**
 * Created by neo on 2015-03-01.
 */

trait CashFlow extends Event {
  def amount: Double
  def tradingExCoupon(refDate: LocalDate = Settings.evaluationDate ): Boolean = this.exCouponDate <= refDate
  def exCouponDate: LocalDate
}

object CashFlow {

  type Leg = List[CashFlow]

  implicit class CashFlowOperation(val cf: CashFlow) extends AnyVal {
    def <(other: CashFlow) = {
      cf.date < other.date
    }
  }
}