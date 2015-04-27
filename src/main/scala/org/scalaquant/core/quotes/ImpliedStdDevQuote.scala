package org.scalaquant.core.quotes

import org.scalaquant.core.instruments.options.Option
import org.scalaquant.core.pricingengines.BlackFormula

case class ImpliedStdDevQuote(value: Double) extends ValidQuote{
  override def map(f: (Double) => Double): Quote = if (isValid) ImpliedStdDevQuote(f(value)) else InvalidQuote
}

object ImpliedStdDevQuote{
  def apply(optionType: Option.Type,
            forward: Quote,
            price: Quote,
            strike: Double,
            guess: Double,
            accuracy: Double = 1.0E-6,
            maxIter: Int = 100): Quote = {
    for{
      fValue <- forward
      pValue <- price
    } yield {
      val impliedValue = BlackFormula.impliedStdDev(
        optionType,
        strike,
        fValue,
        pValue,
        discount = 1.0,
        displacement = 0.0,
        guess,
        accuracy,
        maxIter
      )
      ImpliedStdDevQuote(impliedValue)
    }
  }
}