package org.scalaquant.core.instruments.options

import org.scalaquant.common.Exercise
import org.scalaquant.core.instruments.Instrument
import org.scalaquant.core.instruments.payoffs.Payoff
import org.scalaquant.core.pricingengines.PricingEngine
import scala.language.implicitConversions


class Option[T](payoff: Payoff, exercise: Exercise) extends Instrument[T]

object Option{
  sealed abstract class Type(val value: Int, val other: Type)

  case object Call extends Type(value = 1, other = Put)
  case object Put extends Type(value = -1, other = Call)

  class Arguments(val payoff: Payoff, val exercise: Exercise) extends PricingEngine.Arguments

//  implicit def typeToString(optionType: Type): String = {
//    optionType match {
//      case Call => "call option"
//      case Put => "put option"
//    }
//  }

  implicit class typeOps(optionType: Type) extends AnyVal{
    def *(other: Double):Double = optionType.value * other
    //def /(other: Double):Double = optionType.value * other
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
