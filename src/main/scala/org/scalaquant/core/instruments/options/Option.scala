package org.scalaquant.core.instruments.options

import org.scalaquant.core.common.Exercise
import org.scalaquant.core.instruments.Instrument
import org.scalaquant.core.instruments.payoffs.Payoff
import org.scalaquant.core.pricingengines.PricingEngine


class Option[T](payoff: Payoff, exercise: Exercise) extends Instrument[T]

object Option{
  sealed trait Type {
    def value: Int
  }
  case object Call extends Type{
    final val value = 1
  }

  case object Put extends Type{
    final val value = -1
  }

  class Arguments(val payoff: Payoff, val exercise: Exercise) extends PricingEngine.Arguments

  implicit def typeToString(optionType: Type): String = {
    optionType match {
      case Call => "call option"
      case Put => "put option"
    }
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
