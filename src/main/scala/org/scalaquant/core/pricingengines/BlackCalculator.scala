package org.scalaquant.core.pricingengines


import org.scalaquant.core.instruments.payoffs._

import org.scalaquant.math.Constants._
import org.scalaquant.math.Comparison._
import org.scalaquant.math.distributions._
import org.scalaquant.core.instruments.options.Option

class BlackCalculator(payoff :StrikedTypePayoff, val forward: Double, val stdDev: Double, val discount: Double = 1.0 ) {

  require(payoff.strike >= 0.0, s"strike (${payoff.strike}) must be non-negative")
  require(forward > 0.0, s"forward ($forward) must be positive")
  require(stdDev >= 0.0, s"stdDev ($stdDev) must be non-negative")
  require(discount > 0.0, s"discount ($discount) must be positive")

  private val variance = stdDev * stdDev
  private val f = new CumulativeNormalDistribution()
  private val (d1, d2, cum_d1, cum_d2, n_d1, n_d2) =
    if (stdDev>=QL_EPSILON) {
      if ( payoff.strike ~= 0.0 ) {
        (Double.MaxValue, Double.MaxValue, 1.0, 1.0, 0.0, 0.0)
      } else {
        val d1_ = math.log(forward / payoff.strike )/ stdDev + 0.5 * stdDev
        val d2_ = d1_ - stdDev
        (d1_, d2_, f(d1_), f(d2_), f.derivative(d1_), f.derivative(d2_))
      }
    } else {
      if (forward ~= payoff.strike) {
        (0,0, 0.5, 0.5, M_SQRT_2 * M_1_SQRTPI, M_SQRT_2 * M_1_SQRTPI)
      } else if (forward > payoff.strike) {
        ( Double.MaxValue,  Double.MaxValue, 1.0, 1.0, 0.0, 0.0 )
      } else {
        ( Double.MinValue,  Double.MinValue, 0.0, 0.0, 0.0, 0.0 )
      }
    }

  private def defaults = payoff.optionType match {
    case Option.Call => (cum_d1, n_d1, -cum_d2, -n_d2)
    case Option.Put => (cum_d1-1.0, n_d1, 1.0-cum_d2, -n_d2 )
    case _ =>(Double.NaN, Double.NaN, Double.NaN, Double.NaN)
  }
  private val (x, xDstrike, _alpha, alphaDd1, _beta, betaDd2) = payoff match {
    case CashOrNothingPayoff(optionType, _, cashPayoff) =>
      val (beta, betaDd2) = optionType match {
        case Option.Call => (cum_d2, n_d2)
        case Option.Put => (1.0 - cum_d2, -n_d2)
        case _ => (Double.NaN, Double.NaN)
      }
      (cashPayoff, 0.0, 0.0, 0.0, beta, betaDd2)
    case AssetOrNothingPayoff(optionType,strike) =>
      val (alpha, alphaDd1) =  optionType match {
        case Option.Call => (cum_d1,n_d1)
        case Option.Put =>(1.0 - cum_d1, -n_d1)
        case _ => (Double.NaN, Double.NaN)
      }
      (strike, 1.0, alpha, alphaDd1, 0.0, 0.0)
    case GapPayoff(_,_,secondStrike) =>
      val (alpha, alphaDd1, beta, betaDd2) = defaults
      (secondStrike, 0.0, alpha, alphaDd1, beta, betaDd2)
    case _ =>
      val (alpha, alphaDd1, beta, betaDd2) = defaults
      (payoff.strike, 1.0, alpha, alphaDd1, beta, betaDd2)
  }


  // the following one will probably disappear as soon as
  // super-share will be properly handled
  private val xDs = 0.0

  val value: Double = discount * (forward * alpha + x * beta)

  /*! Sensitivity to change in the underlying forward price. */
  val deltaForward: Double = {
    val temp = stdDev * forward
    val alphaDforward = alphaDd1 / temp
    val betaDforward  = betaDd2 / temp
    val temp2 = alphaDforward * forward + alpha + betaDforward  * x // DXDforward = 0.0

    discount * temp2
  }

  /*! Sensitivity to change in the underlying spot price. */
  def delta(spot: Double): Double = {
    require(spot > 0.0, s"positive spot value required: $spot not allowed")
    val forwardDs = forward / spot

    val temp = stdDev * spot
    val alphaDs = alphaDd1 / temp
    val betaDs  = betaDd2 /temp
    val temp2 = alphaDs * forward + alpha * forwardDs + betaDs  * x + beta  * xDs

     discount * temp2
  }

  /*! Sensitivity in percent to a percent change in the
      underlying forward price. */
  val elasticityForward: Double = {

    if (value > QL_EPSILON)
      deltaForward / value * forward
    else if (math.abs(deltaForward) < QL_EPSILON)
       0.0
    else if (deltaForward>0.0)
       Double.MaxValue
    else
       Double.MinValue
  }

  /*! Sensitivity in percent to a percent change in the
      underlying spot price. */
  def elasticity(spot: Double): Double = {
    val del = delta(spot)
    if (value > QL_EPSILON)
     del / value * spot
    else if (math.abs(del) < QL_EPSILON)
       0.0
    else if (del>0.0)
       Double.MaxValue
    else
       Double.MinValue
  }
  /*! Second order derivative with respect to change in the
      underlying forward price. */
  val gammaForward: Double = {
    val temp = stdDev * forward
    val alphaDforward = alphaDd1 / temp
    val betaDforward  = betaDd2 / temp

    val D2alphaDforward2 = - alphaDforward / forward * (1 + d1 / stdDev)
    val D2betaDforward2  = - betaDforward / forward * (1 + d2 / stdDev)

    val temp2 = D2alphaDforward2 * forward + 2.0 * alphaDforward + D2betaDforward2  * x // DXDforward = 0.0

    discount * temp2
  }
  /*! Second order derivative with respect to change in the
      underlying spot price. */
  def gamma(spot: Double): Double = {
    require(spot > 0.0, s"positive spot value required: $spot not allowed")

    val DforwardDs = forward / spot

    val temp = stdDev * spot
    val DalphaDs = alphaDd1 / temp
    val DbetaDs  = betaDd2 / temp

    val D2alphaDs2 = -DalphaDs/spot*(1+d1/stdDev)
    val D2betaDs2  = -DbetaDs /spot*(1+d2/stdDev)

    val temp2 = D2alphaDs2 * forward + 2.0 * DalphaDs * DforwardDs +D2betaDs2  * x      + 2.0 * DbetaDs  * xDs

    discount * temp2
  }
  /*! Sensitivity to time to maturity. */
  def theta(spot: Double, maturity: Double): Double = {
    require(maturity>=0.0, s"maturity ($maturity) must be non-negative")

    if (maturity ~= 0.0)
      0.0
    else -(math.log(discount)* value + math.log(forward/spot) * spot * delta(spot) + 0.5 * variance * spot  * spot * gamma(spot))/maturity
  }
  /*! Sensitivity to time to maturity per day,
      assuming 365 day per year. */
  def thetaPerDay(spot: Double, maturity: Double): Double =  theta(spot,maturity) / 365.0
  /*! Sensitivity to volatility. */
  def vega(maturity: Double): Double = {
    require(maturity>=0.0, s"maturity ($maturity) must be non-negative")

    val temp = math.log(payoff.strike / forward) / variance
    // actually DalphaDsigma / SQRT(T)
    val DalphaDsigma = alphaDd1*(temp+0.5)
    val DbetaDsigma  = betaDd2 *(temp-0.5)

    val temp2 = DalphaDsigma * forward + DbetaDsigma * x

    discount * math.sqrt(maturity) * temp2
  }
  /*! Sensitivity to discounting rate. */
  def rho(maturity: Double): Double = {
    require(maturity>=0.0, s"maturity ($maturity) must be non-negative")

    // actually DalphaDr / T
    val DalphaDr = alphaDd1/stdDev
    val DbetaDr  = betaDd2/stdDev
    val temp = DalphaDr * forward + alpha * forward + DbetaDr * x

    maturity * (discount * temp - value)
  }
  /*! Sensitivity to dividend/growth rate. */
  def dividendRho(maturity: Double): Double = {
    require(maturity>=0.0, s"maturity ($maturity) must be non-negative")

    // actually DalphaDq / T
    val DalphaDq = -alphaDd1/stdDev
    val DbetaDq  = -betaDd2/stdDev

    val temp = DalphaDq * forward - alpha * forward + DbetaDq * x

    maturity * discount * temp

  }
  /*! Probability of being in the money in the bond martingale
      measure, i.e. N(d2).
      It is a risk-neutral probability, not the real world one.
  */
  val itmCashProbability: Double = cum_d2
  /*! Probability of being in the money in the asset martingale
      measure, i.e. N(d1).
      It is a risk-neutral probability, not the real world one.
  */
  val itmAssetProbability: Double = cum_d1
  /*! Sensitivity to strike. */
  def strikeSensitivity: Double = {
    val temp = stdDev * payoff.strike
    val DalphaDstrike = -alphaDd1/temp
    val DbetaDstrike  = -betaDd2/temp

    val temp2 = DalphaDstrike * forward + DbetaDstrike * x + beta * xDstrike

    discount * temp2
  }
  def alpha: Double = _alpha
  def beta: Double = _beta
}
