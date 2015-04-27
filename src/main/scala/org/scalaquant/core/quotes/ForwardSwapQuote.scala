package org.scalaquant.core.quotes

import org.scalaquant.core.common.Settings
import org.scalaquant.core.common.time.BusinessDayConvention.Following
import org.scalaquant.core.common.time.Period
import org.scalaquant.core.indexes.swap.SwapIndex

case class ForwardSwapQuote(swapIndex: SwapIndex, spread: Quote, fwdStart: Period) extends Quote{
  private val evaluationDate = Settings.evaluationDate
  private val swap = swapIndex.underlyingSwap(fixingDate)
  val valueDate = swapIndex.fixingCalendar.advance(evaluationDate,swapIndex.fixingDays*, Following)
  val startDate = swapIndex.fixingCalendar.advance(valueDate, fwdStart.length, fwdStart.units, Following)
  val fixingDate = swapIndex.fixingDate(startDate)


}

object ForwardSwapQuote{
  def apply(swapIndex: SwapIndex, spread: Quote, fwdStart: Period): Quote = {

  }
}