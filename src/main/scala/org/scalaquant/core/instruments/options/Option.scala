package org.scalaquant.core.instruments.options


import scala.language.implicitConversions

object Option{

  sealed abstract class Type(val value: Int){
    def other: Type
    def *(value: Double): Double = this.value * value
    def /(value: Double): Double = this.value * value
  }

  case object Call extends Type(1){ def other = Put }
  case object Put extends Type(-1){ def other = Call }

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
