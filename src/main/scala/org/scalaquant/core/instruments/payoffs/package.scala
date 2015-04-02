package org.scalaquant.core.instruments

import org.scalaquant.core.instruments.options.Option

import scala.language.implicitConversions

package object payoffs {

  trait Payoff extends (Double => Double) {
    def name: String
    def description: String
  }

  sealed trait TypePayoff extends Payoff{
    def optionType: Option.Type
    def description: String = name + " " + optionType

    protected def optionPayOff(callPayoff: => Double, putPayoff: => Double) = optionType match {
      case Option.Call => callPayoff
      case Option.Put  => putPayoff
      case _ => Double.NaN
    }
  }

  trait StrikedTypePayoff extends TypePayoff{
    def strike: Double
    override def description = s"${super.description}, $strike strike"
  }
  case class FloatingTypePayoff(optionType: Option.Type) extends TypePayoff{
    val name: String = "FloatingType"
  }

  case class PlainVanillaPayoff(optionType: Option.Type, strike: Double) extends StrikedTypePayoff{
    val name = "Vanilla"
    def apply(price: Double): Double = optionPayOff(Math.max(price - strike, 0.0), Math.max(strike - price, 0.0))
  }

  case class PercentageStrikePayoff(optionType: Option.Type, private val moneyness: Double) extends StrikedTypePayoff{
    val name = "PercentageStrike"
    val strike = moneyness
    def apply(price: Double): Double = optionPayOff(Math.max(1.0 - strike, 0.0), Math.max(strike - 1.0, 0.0))
  }

  case class AssetOrNothingPayoff(optionType: Option.Type, strike: Double) extends StrikedTypePayoff{
    val name = "AssetOrNothing"
    def apply(price: Double): Double = optionPayOff(if (price - strike > 0.0) price else 0.0, if (strike - price > 0.0) price else 0.0)
  }

  case class CashOrNothingPayoff(optionType: Option.Type, strike: Double, cashPayoff: Double) extends StrikedTypePayoff{
    val name = "CashOrNothing"
    override val description = s"${super.description}, $cashPayoff cash payoff "
    def apply(price: Double): Double = optionPayOff(if (price - strike > 0.0) cashPayoff else 0.0,if (strike - price > 0.0) cashPayoff else 0.0)
  }

  case class GapPayoff(optionType: Option.Type, strike: Double, secondStrike: Double) extends StrikedTypePayoff{
    val name = "Gap"
    override val description = s"${super.description}, $secondStrike second strike payoff "
    def apply(price: Double): Double = optionPayOff(if (price - strike >= 0.0) price - secondStrike else 0.0, if (strike - price >= 0.0) secondStrike - price else 0.00
  }

  case class SuperFundPayoff(strike: Double, secondStrike: Double) extends StrikedTypePayoff{
    require(strike>0.0, s"strike ($strike) must be positive")
    require(secondStrike>strike, s"second strike ($secondStrike) must be higher than first strike ($strike)")
    val optionType = Option.Call
    val name = "SuperFund"
    def apply(price: Double): Double = if (price>=strike && price<secondStrike) price/strike else 0.0
  }

  case class SuperSharePayoff(strike: Double, secondStrike: Double, cashPayoff: Double) extends StrikedTypePayoff{
    require(secondStrike>strike, s"second strike ($secondStrike) must be higher than first strike ($strike)")
    val optionType = Option.Call
    val name = "SuperShare"
    override val description = s"${super.description}, $secondStrike second strike payoff, $cashPayoff amount"
    def apply(price: Double): Double = if (price>=strike && price<secondStrike) cashPayoff else 0.0
  }

}
