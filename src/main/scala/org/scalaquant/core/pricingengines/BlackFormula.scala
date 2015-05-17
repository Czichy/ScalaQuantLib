package org.scalaquant.core.pricingengines

import org.scalaquant.core.instruments.options.Option
import org.scalaquant.core.instruments.options.Option.{Call, Put}
import org.scalaquant.math.Constants
import org.scalaquant.math.Comparison._
import scala.math._
import org.scalaquant.core.instruments.payoffs.PlainVanillaPayoff
import org.scalaquant.math.distributions.{NormalDistribution, CumulativeNormalDistribution}

object BlackFormula {

  private def checkParameters(implicit strike: Double, forward: Double, displacement: Double): Unit = {
    require(displacement >= 0.0, s"displacement ($displacement) must be non-negative" )
    require(strike + displacement >= 0.0, s"strike + displacement ($strike + $displacement) must be non-negative")
    require(forward + displacement > 0.0, s"forward + displacement ($forward + $displacement) must be positive")
  }

  private def checkStdDev(implicit stdDeviation: Double): Unit = {
    require(stdDeviation>=0.0, s"stdDev ($stdDeviation) must be non-negative")
  }

  private def checkDiscount(implicit discount: Double): Unit = {
    require(discount>0.0, s"stdDev ($discount) must be positive")
  }

  private def checkBlackPrice(implicit blackPrice: Double): Unit = {
    require(blackPrice>=0.0, s"blackPrice ($blackPrice) must be non-negative")
  }

  private def checkBlackAtmPrice(implicit blackAtmPrice: Double): Unit = {
    require(blackAtmPrice >= 0.0, s"blackAtmPrice ($blackAtmPrice) must be non-negative")
  }
  /**
   *  Black 1976 formula
   *  warning instead of volatility it uses standard deviation,
   *  i.e. volatility*sqrt(timeToMaturity)
   **/
  def apply(optionType: Option.Type,
             strike: Double,
             forward: Double,
             stdDeviation: Double,
             discount: Double = 1.0,
             displacement: Double = 0.0): Double = {

    checkParameters
    checkStdDev
    checkDiscount

    if (stdDeviation == 0.0) {
     max((forward - strike) * optionType.value, 0.0) * discount
    } else {
      val _forward = forward + displacement
      val _strike = strike + displacement

      // since displacement is non-negative strike==0 iff displacement==0
      // so returning forward*discount is OK
      if (_strike == 0.0) {
        if (optionType == Option.Call) _forward * discount else 0.0
      } else {
        val d1 = log(_forward / _strike) / stdDeviation + 0.5 * stdDeviation
        val d2 = d1 - stdDeviation
        val phi = CumulativeNormalDistribution()
        val nd1 = phi(optionType * d1)
        val nd2 = phi(optionType * d2)

        discount * (optionType * (_forward * nd1 - _strike * nd2))
      }
    }
  } ensuring (_ >= 0.0, s"negative value for $stdDeviation stdDev, $optionType option, $strike strike $forward forward")

  /** Black 1976 formula
    * warning instead of volatility it uses standard deviation,
    * i.e. volatility*sqrt(timeToMaturity)
    **/
  def apply(payoff: PlainVanillaPayoff,
                   forward: Double,
                   stdDeviation: Double,
                   discount: Double = 1.0,
                   displacement: Double = 0.0): Double = {
    apply(payoff.optionType,
         payoff.strike,
         forward,
         stdDeviation,
         discount,
         displacement)
  }


  /** Approximated Black 1976 implied standard deviation,
   *   i.e. volatility*sqrt(timeToMaturity).
   *   It is calculated using Brenner and Subrahmanyan (1988) and Feinstein
   *   (1988) approximation for at-the-money forward option, with the
   *   extended moneyness approximation by Corrado and Miller (1996)
   **/
  def impliedStdDevApproximation(optionType: Option.Type,
                                             strike: Double,
                                             forward: Double,
                                             blackPrice: Double,
                                             discount: Double = 1.0,
                                             displacement: Double = 0.0): Double = {
    checkParameters
    checkBlackPrice
    checkDiscount

    val _forward = forward + displacement
    val _strike = strike + displacement
    if (_strike == _forward)
    // Brenner-Subrahmanyan (1988) and Feinstein (1988) ATM approx.
      blackPrice / discount * sqrt(2.0 * Constants.M_PI) / _forward
    else {
      // Corrado and Miller extended moneyness approximation
      val moneynessDelta = optionType * (_forward - _strike)
      val moneynessDelta_2 = moneynessDelta / 2.0
      val temp = blackPrice / discount - moneynessDelta_2
      val moneynessDelta_PI = moneynessDelta * moneynessDelta / Constants.M_PI
      val temp2 = temp * temp - moneynessDelta_PI
      val temp3 = if (temp2 < 0.0) 0.0 else temp2// approximation breaks down, 2 alternatives: // 1. zero it
        // 2. Manaster-Koehler (1982) efficient Newton-Raphson seed
      //return abs(log(_forward/strike))*sqrt(2.0);

      (temp + sqrt(temp3)) * sqrt(2.0 * Constants.M_PI) / (_forward + _strike)
    }
  } ensuring (_ >= 0.0, s"stdDev must be non-negative")



  /** Approximated Black 1976 implied standard deviation,
   *   i.e. volatility*sqrt(timeToMaturity).
   *   It is calculated using Brenner and Subrahmanyan (1988) and Feinstein
   *   (1988) approximation for at-the-money forward option, with the
   *   extended moneyness approximation by Corrado and Miller (1996)
  **/
  def impliedStdDevApproximation(payoff: PlainVanillaPayoff,
                                             forward: Double,
                                             blackPrice: Double,
                                             discount: Double = 1.0,
                                             displacement: Double = 0.0): Double = {
    impliedStdDevApproximation(payoff.optionType,
      payoff.strike,
      forward,
      blackPrice,
      discount,
      displacement)
  }


  def impliedStdDevChambers(optionType:Option.Type,
                            strike: Double,
                            forward: Double,
                            blackPrice: Double,
                            blackAtmPrice: Double,
                            discount: Double,
                            displacement: Double): Double = {

      checkParameters
      checkBlackPrice
      checkBlackAtmPrice
      checkDiscount

      val _forward = forward + displacement
      val _strike = strike + displacement
      val _blackPrice = blackPrice / discount
      val _blackAtmPrice = blackAtmPrice / discount

      val s0 = Constants.M_SQRT2 * Constants.M_SQRTPI * _blackAtmPrice / _forward // Brenner-Subrahmanyam formula
      val priceAtmVol = apply(optionType, _strike, _forward, s0, 1.0, 0.0)
      val dc = _blackPrice - priceAtmVol

      if (dc ~= 0.0) {
        s0
      } else {
        val d1 = stdDevDerivative(_strike, _forward, s0, 1.0, 0.0)
        val d2 = stdDevSecondDerivative(_strike, _forward, s0, 1.0, 0.0)
        val tmp = d1 * d1 + 2.0 * d2 * dc

        val ds =
          if (abs(d2) > 1E-10 && tmp >= 0.0) (-d1 + sqrt(tmp)) / d2 // second order approximation
          else if (abs(d1) > 1E-10) dc / d1 // first order approximation
          else 0.0

        s0 + ds
      }
  } ensuring (_ >= 0.0, s"stdDev must be non-negative")


  private case class ImpliedStdDevHelper(optionType: Option.Type,
                                         strike: Double,
                                         forward: Double,
                                         undiscountedBlackPrice: Double,
                                         displacement: Double = 0.0) extends (Double => Double) {
    checkParameters
    checkBlackPrice(undiscountedBlackPrice)

    private val halfOptionType = optionType * 0.5
    private val signedStrike = optionType * (strike + displacement)
    private val signedForward = optionType * (forward + displacement)
    private val N = CumulativeNormalDistribution()
    private val signedMoneyness = optionType * log((forward+displacement)/(strike+displacement))

    def apply(stdDev: Double): Double = {
      checkStdDev

      if (stdDev == 0.0) {
        max(signedForward - signedStrike, 0.0) - undiscountedBlackPrice
      } else {
        val temp = halfOptionType * stdDev
        val d = signedMoneyness / stdDev
        val signedD1 = d + temp
        val signedD2 = d - temp
        val result = signedForward * N(signedD1) - signedStrike * N(signedD2)
        // numerical inaccuracies can yield a negative answer
        max(0.0, result) - undiscountedBlackPrice
      }
    }

    def derivative(stdDev: Double): Double ={
       signedForward * N.derivative(signedMoneyness / stdDev + halfOptionType * stdDev)
    }
  }
  /** Black 1976 implied standard deviation,
   *  i.e. volatility*sqrt(timeToMaturity)
   */
  def impliedStdDev(optionType: Option.Type,
                                strike: Double,
                                forward: Double,
                                blackPrice: Double,
                                discount: Double = 1.0,
                                displacement: Double = 0.0,
                                guess: Double,
                                accuracy: Double = 1.0e-6,
                                maxIterations: Int = 100): Double = {
    checkParameters
    checkDiscount
    checkBlackPrice
    require(guess>=0.0, s"stdDev guess ($guess) must be non-negative")
    val otherOptionPrice = blackPrice - optionType * (forward-strike) * discount
    require(otherOptionPrice >= 0.0,
        s"negative ${optionType.other} price ($otherOptionPrice) implied by put-call parity. " +
        s"No solution exists for $optionType strike $strike, forward $forward, price $blackPrice, deflator $discount")

    // solve for the out-of-the-money option which has
    // greater vega/price ratio, i.e.
    // it is numerically more robust for implied vol calculations
    val (_optionType, _blackPrice) = if (strike>forward) {
      if (optionType==Put) (Call, otherOptionPrice) else (Put, blackPrice)
    } else if (strike<forward) {
      if (optionType==Call) (Put, otherOptionPrice) else (Call, blackPrice)
    } else {
      (optionType, blackPrice)
    }

    val _strike = strike + displacement
    val _forward = forward + displacement
    val _guess = impliedStdDevApproximation(_optionType, _strike, _forward, _blackPrice, discount, displacement)

    ImpliedStdDevHelper(_optionType, _strike, _forward, _blackPrice/discount)
    NewtonSafe solver;
    solver.setMaxEvaluations(maxIterations)
    val minSdtDev = 0.0
    val maxStdDev = 24.0 // 24 = 300% * sqrt(60)
     solver.solve(f, accuracy, guess, minSdtDev, maxStdDev);

  } ensuring (_ >= 0.0, "stdDev must be non-negative")

  /** Black 1976 implied standard deviation,
   *  i.e. volatility*sqrt(timeToMaturity)
   */
  def impliedStdDev(payoff: PlainVanillaPayoff,
                                forward: Double,
                                blackPrice: Double,
                                discount: Double = 1.0,
                                displacement: Double = 0.0,
                                guess: Double,
                                accuracy: Double = 1.0e-6,
                                maxIterations: Int = 100): Double = {

  }


  /** Black 1976 probability of being in the money (in the bond martingale
    * measure), i.e. N(d2).
    * It is a risk-neutral probability, not the real world one.
    * \warning instead of volatility it uses standard deviation,
    *             i.e. volatility*sqrt(timeToMaturity)
    */
  def cashItmProbability(optionType: Option.Type,
                                      strike: Double,
                                      forward: Double,
                                      stdDeviation: Double,
                                      displacement: Double = 0.0): Double = ???

  /** Black 1976 probability of being in the money (in the bond martingale
    *  measure), i.e. N(d2).
    *  It is a risk-neutral probability, not the real world one.
    *  \warning instead of volatility it uses standard deviation,
    *           i.e. volatility*sqrt(timeToMaturity)
    */
  def cashItmProbability(payoff: PlainVanillaPayoff,
                                     forward: Double,
                                     stdDeviation: Double,
                                     displacement: Double = 0.0): Double = ???

  /*! Black 1976 formula for standard deviation derivative
      \warning instead of volatility it uses standard deviation, i.e.
               volatility*sqrt(timeToMaturity), and it returns the
               derivative with respect to the standard deviation.
               If T is the time to maturity Black vega would be
               blackStdDevDerivative(strike, forward, stdDev)*sqrt(T)
  */
  def stdDevDerivative(strike: Double,
                       forward: Double,
                       stdDev: Double,
                       discount: Double = 1.0,
                       displacement: Double = 0.0): Double = ???

  def stdDevSecondDerivative(strike: Double,
                             forward: Double,
                             stdDev: Double,
                             discount: Double = 1.0,
                             displacement: Double = 0.0): Double = {
    checkParameters
    checkStdDev
    checkDiscount

    val _forward = forward + displacement
    val _strike = strike + displacement

    if (stdDev==0.0 || _strike==0.0) {
      0.0
    } else {
      val d1 = log(_forward/_strike)/stdDev + .5*stdDev
      val d1p = -log(_forward/_strike)/(stdDev*stdDev) + .5
      discount * _forward * (new NormalDistribution()).derivative(d1) * d1p
    }
  }

  def stdDevSecondDerivative(payoff: PlainVanillaPayoff,
                             forward: Double,
                             stdDev: Double,
                             discount: Double = 1.0,
                             displacement: Double = 0.0): Double = {
    stdDevSecondDerivative(payoff.strike, forward, stdDev, discount, displacement)
  }

  /*! Black 1976 formula for  derivative with respect to implied vol, this
      is basically the vega, but if you want 1% change multiply by 1%
 */
  def volDerivative(Real strike,
    Real forward,
    Real stdDev,
    Real expiry,
    Real discount = 1.0,
    Real displacement = 0.0);


  /*! Black 1976 formula for standard deviation derivative
      \warning instead of volatility it uses standard deviation, i.e.
               volatility*sqrt(timeToMaturity), and it returns the
               derivative with respect to the standard deviation.
               If T is the time to maturity Black vega would be
               blackStdDevDerivative(strike, forward, stdDev)*sqrt(T)
  */
  def stdDevDerivative(
    const boost::shared_ptr<PlainVanillaPayoff>& payoff,
    Real forward,
    Real stdDev,
    Real discount = 1.0,
    Real displacement = 0.0);


  /*! Black style formula when forward is normal rather than
      log-normal. This is essentially the model of Bachelier.

      \warning Bachelier model needs absolute volatility, not
               percentage volatility. Standard deviation is
               absoluteVolatility*sqrt(timeToMaturity)
  */
  def bachelier(Option::Type optionType,
    Real strike,
    Real forward,
    Real stdDev,
    Real discount = 1.0);

  /*! Black style formula when forward is normal rather than
      log-normal. This is essentially the model of Bachelier.

      \warning Bachelier model needs absolute volatility, not
               percentage volatility. Standard deviation is
               absoluteVolatility*sqrt(timeToMaturity)
  */
  def bachelier(
    const boost::shared_ptr<PlainVanillaPayoff>& payoff,
    Real forward,
    Real stdDev,
    Real discount = 1.0);
  /*! Approximated Bachelier implied volatility

      It is calculated using  the analytic implied volatility approximation
      of J. Choi, K Kim and M. Kwak (2009), “Numerical Approximation of the
      Implied Volatility Under Arithmetic Brownian Motion”,
      Applied Math. Finance, 16(3), pp. 261-268.
  */
  def bachelierimpliedVol(Option::Type optionType,
    Real strike,
    Real forward,
    Real tte,
    Real bachelierPrice,
    Real discount = 1.0);

}
}
