package org.scalaquant.core.quotes

import org.joda.time.LocalDate
import org.scalaquant.core.common.Settings
import org.scalaquant.core.indexes.ibor.IborIndex

/**
 * Created by neo on 2015-04-27.
 */
case class FuturesConvAdjustmentQuote(value: Double,
                                      futuresValue: Double,
                                      volatility: Double,
                                      meanReversion: Double,
                                      immDate: LocalDate) extends ValidQuote {
  override def map(f: (Double) => Double): Quote = if (isValid) copy(f(value)) else InvalidQuote

}


object FuturesConvAdjustmentQuote{
  def apply(index: IborIndex,
            futuresDate: LocalDate,
            futuresQuote: Quote,
            volatility: Quote,
            meanReversion: Quote): Quote ={
    for{
      fValue <- futuresQuote
      vValue <- volatility
      meanValue <- meanReversion
    } yield {
      val dc = index.dayCounter
      val settlementDate = Settings.evaluationDate
      val startTime = dc.fractionOfYear(settlementDate, futuresDate, futuresDate)
      val indexMaturity = dc.fractionOfYear(settlementDate, index.maturityDate(futuresDate), index.maturityDate(futuresDate))
      val value = NaN
      FuturesConvAdjustmentQuote(value,fValue,vValue,meanValue,futuresDate)
    }
  }

  def apply(index: IborIndex,
            immCode: String,
            futuresQuote: Quote,
            volatility: Quote,
            meanReversion: Quote): Quote = ???
}