package org.scalaquant.core.instruments.options

import org.scalaquant.core.common.Exercise
import org.scalaquant.core.instruments.Instrument
import org.scalaquant.core.instruments.payoffs.Payoff
import org.scalaquant.core.pricingengines.PricingEngine
import scala.language.implicitConversions


class Option[T](payoff: Payoff, exercise: Exercise)

object Option{

  sealed abstract class OptionType(val value: Int){ def other: OptionType }

  case object Call extends OptionType(1){ def other = Put }
  case object Put extends OptionType(-1){ def other = Call }

//  class Arguments(val payoff: Payoff, val exercise: Exercise) extends PricingEngine.Arguments
//
////  implicit def typeToString(optionType: Type): String = {
////    optionType match {
////      case Call => "call option"
////      case Put => "put option"
////    }
////  }
//
//  implicit class typeOps(optionType: Type) extends AnyVal{
//    def *(other: Double):Double = optionType.value * other
//    //def /(other: Double):Double = optionType.value * other
//  }

  trait Greeks {
    def delta: Double
    def gamma: Double
    def theta: Double
    def vega: Double
    def rho: Double
    def dividendRho: Double
  }

  trait MoreGreeks {
    def itmCashProbability: Double
    def deltaForward: Double
    def elasticity: Double
    def thetaPerDay: Double
    def strikeSensitivity: Double
  }
}
