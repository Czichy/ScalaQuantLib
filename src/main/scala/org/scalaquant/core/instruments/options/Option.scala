package org.scalaquant.core.instruments.options

import org.scalaquant.core.common.{Exercise, Payoff}
import org.scalaquant.core.instruments.Instrument
import org.scalaquant.core.pricingengines.PricingEngine


class Option[T](payoff: Payoff, exercise: Exercise) extends Instrument[T]

object Option{

  sealed class Type(value:Int) extends AnyVal
  val Call = new Type(1)
  val Put = new Type(-1)

  class Arguments(val payoff: Payoff, val exercise: Exercise) extends PricingEngine.Arguments {
    override def validated: Boolean = true
  }

  trait Greeks extends PricingEngine.Results {
      def delta: Double
      def gamma: Double
      def theta: Double
      def vega: Double
      def rho: Double
      def dividendRho: Double
  }

   trait MoreGreeks extends PricingEngine.Results{
     def itmCashProbability: Double
     def deltaForward: Double
     def elasticity: Double
     def thetaPerDay: Double
     def strikeSensitivity: Double
   }
}
