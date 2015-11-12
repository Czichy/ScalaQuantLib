package org.scalaquant.core.instruments

import org.scalaquant.core.instruments.options.Option
import scala.math._
import scala.language.implicitConversions

package object payoffs {
  
  trait Payoff extends (Double => Double) {
    def name: String
    def description: String
  }

  sealed abstract class TypedPayoff(val name: String, optionType: Option.Type) extends Payoff{

    def description: String = name + " " + optionType

    protected def payoff(callCalculation: => Double, putCalculation: => Double) = optionType match {
      case Option.Call => callCalculation
      case Option.Put  => putCalculation
    }
  }

  abstract class StrikedPayoff(name: String, optionType: Option.Type) extends TypedPayoff(name, optionType){
    def strike: Double
    override def description = s"${super.description}, $strike strike"
  }

  case class FloatingTypePayoff(optionType: Option.Type) extends TypedPayoff("FloatingType", optionType)

  case class PlainVanillaPayoff(optionType: Option.Type, strike: Double) extends StrikedPayoff("Vanilla", optionType) {
    def apply(price: Double): Double = payoff(max(price - strike, 0.0), max(strike - price, 0.0))
  }

  case class PercentageStrikePayoff(optionType: Option.Type, private val moneyness: Double) extends StrikedPayoff(moneyness, "PercentageStrike", optionType){
    def apply(price: Double): Double = payoff(max(1.0 - moneyness, 0.0), max(moneyness - 1.0, 0.0))
  }

  case class AssetOrNothingPayoff(optionType: Option.Type, strike: Double) extends StrikedPayoff("AssetOrNothing", optionType){
    def apply(price: Double): Double = payoff(if (price - strike > 0.0) price else 0.0, if (strike - price > 0.0) price else 0.0)
  }

  case class CashOrNothingPayoff(optionType: Option.Type, strike: Double, cashPayoff: Double) extends StrikedPayoff("CashOrNothing", optionType){
    override def description = s"${super.description}, $cashPayoff cash payoff "
    def apply(price: Double): Double = payoff(if (price - strike > 0.0) cashPayoff else 0.0, if (strike - price > 0.0) cashPayoff else 0.0)
  }

  case class GapPayoff(optionType: Option.Type, strike: Double, secondStrike: Double) extends StrikedPayoff("Gap", optionType){
    override def description = s"${super.description}, $secondStrike second strike payoff "
    def apply(price: Double): Double = payoff(if (price - strike >= 0.0) price - secondStrike else 0.0, if (strike - price >= 0.0) secondStrike - price else 0.00
  }

  case class SuperFundPayoff(strike: Double, secondStrike: Double) extends StrikedPayoff("SuperFund", Option.Call) {
    require(strike>0.0, s"strike ($strike) must be positive")
    require(secondStrike>strike, s"second strike ($secondStrike) must be higher than first strike ($strike)")
    
    def apply(price: Double): Double = if (price>=strike && price<secondStrike) price/strike else 0.0
  }

  case class SuperSharePayoff(strike: Double, secondStrike: Double, cashPayoff: Double) extends StrikedPayoff("SuperShare", Option.Call){
    require(secondStrike>strike, s"second strike ($secondStrike) must be higher than first strike ($strike)")
    
    override def description = s"${super.description}, $secondStrike second strike payoff, $cashPayoff amount"
    def apply(price: Double): Double = if (price>=strike && price<secondStrike) cashPayoff else 0.0
  }

}
