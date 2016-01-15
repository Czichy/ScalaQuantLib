package org.scalaquant.core.quotes

import org.scalaquant.core.instruments.options.Option._
import org.scalaquant.core.pricingengines.BlackFormula
import org.scalaquant.core.instruments.options.Option
import Quote.Calculation

case class EurodollarFuturesImpliedStdDevQuote(value: Double) extends ValidQuote {
  override def map(f: Double => Double): Quote =
    if (isValid)
      EurodollarFuturesImpliedStdDevQuote(f(value))
    else
      InvalidQuote
}

object EurodollarFuturesImpliedStdDevQuote{
  def apply(forward: Quote,
            callPrice: Quote,
            putPrice: Quote,
            strike: Double,
            guess: Double = .15,
            accuracy:Double = 1.0E-6,
            maxIter:Int = 100): Quote = {
    val stikeValue = 100.0 - strike
    val forwardValue = 100.0 - (_: Double)
    def impliedValue(optionType: Option.Type, fValue: Double, pValue: Double) = BlackFormula.impliedStdDev(
      optionType,
      stikeValue,
      100.0 - fValue,
      pValue,
      discount = 1.0,
      displacement = 0.0,
      guess,
      accuracy,
      maxIter
    )

    for{
      fValue <- forward
      pValue <- if (stikeValue > forwardValue(fValue)) putPrice else callPrice
    } yield {
      val value = impliedValue(if (stikeValue > forwardValue(fValue)) Call else Put,fValue,pValue)
      EurodollarFuturesImpliedStdDevQuote(value)
    }
  }
}