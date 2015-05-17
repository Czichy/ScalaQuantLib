package org.scalaquant.core.pricingengines

import org.scalaquant.core.instruments.payoffs.StrikedPayoff

class BlackScholesCalculator(payoff :StrikedPayoff,
                             val spot: Double,
                             val growth: Double,
                             override val stdDev: Double,
                             override val discount: Double = 1.0 ) extends BlackCalculator(payoff, spot*growth/discount, stdDev, discount){
  require(spot > 0.0, s"spot ($spot) must be positive")
  require(growth > 0.0, s"growth ($growth) must be positive")

  def delta: Double = super.delta(spot)
  def elasticity: Double = super.elasticity(spot)
  def gamma: Double = super.gamma(spot)
  def theta(maturity: Double): Double = super.theta(spot, maturity)
  def thetaPerDay(maturity: Double): Double = super.thetaPerDay(spot, maturity)

}
