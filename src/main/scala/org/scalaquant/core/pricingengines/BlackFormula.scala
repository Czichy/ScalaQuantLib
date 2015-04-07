package org.scalaquant.core.pricingengines

import org.scalaquant.core.instruments.options.Option


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
  /**
   *  Black 1976 formula
   *  warning instead of volatility it uses standard deviation,
   *  i.e. volatility*sqrt(timeToMaturity)
   **/
  def blackFormula(optionType: Option.Type,
                   strike: Double,
                   forward: Double,
                   stdDeviation: Double,
                   discount: Double = 1.0,
                   displacement: Double = 0.0): Double = {

    checkParameters
    checkStdDev
    checkDiscount

    if (stdDeviation == 0.0) {
      Math.max((forward - strike) * optionType.value, 0.0) * discount
    } else {
      val _forward = forward + displacement
      val _strike = strike + displacement

      // since displacement is non-negative strike==0 iff displacement==0
      // so returning forward*discount is OK
      if (_strike == 0.0) {
        if (optionType == Option.Call) _forward * discount else 0.0
      } else {
        val d1 = Math.log(_forward / _strike) / stdDeviation + 0.5 * stdDeviation
        val d2 = d1 - stdDeviation
        val phi = CumulativeNormalDistribution()
        val nd1 = phi(optionType.value * d1)
        val nd2 = phi(optionType.value * d2)

        discount * optionType.value * (_forward * nd1 - _strike * nd2)
      }
    }
  } ensuring (_ >= 0.0, s"negative value for $stdDeviation stdDev, $optionType option, $strike strike $forward forward")

  /** Black 1976 formula
    * warning instead of volatility it uses standard deviation,
    * i.e. volatility*sqrt(timeToMaturity)
    **/
  def blackFormula(payoff: PlainVanillaPayoff,
                   forward: Double,
                   stdDeviation: Double,
                   discount: Double = 1.0,
                   displacement: Double = 0.0): Double = ???


  /** Approximated Black 1976 implied standard deviation,
   *   i.e. volatility*sqrt(timeToMaturity).
   *   It is calculated using Brenner and Subrahmanyan (1988) and Feinstein
   *   (1988) approximation for at-the-money forward option, with the
   *   extended moneyness approximation by Corrado and Miller (1996)
   **/
  def blackFormulaImpliedStdDevApproximation(optionType: Option.Type,
                                             strike: Double,
                                             forward: Double,
                                             blackPrice: Double,
                                             discount: Double = 1.0,
                                             displacement: Double = 0.0): Double = ???

  /** Approximated Black 1976 implied standard deviation,
   *   i.e. volatility*sqrt(timeToMaturity).
   *   It is calculated using Brenner and Subrahmanyan (1988) and Feinstein
   *   (1988) approximation for at-the-money forward option, with the
   *   extended moneyness approximation by Corrado and Miller (1996)
  **/
  def blackFormulaImpliedStdDevApproximation(payoff: PlainVanillaPayoff,
                                             forward: Double,
                                             blackPrice: Double,
                                             discount: Double = 1.0,
                                             displacement: Double = 0.0): Double = ???

  /** Black 1976 implied standard deviation,
   *  i.e. volatility*sqrt(timeToMaturity)
   */
  def blackFormulaImpliedStdDev(optionType: Option.Type,
                                strike: Double,
                                forward: Double,
                                blackPrice: Double,
                                discount: Double = 1.0,
                                displacement: Double = 0.0,
                                guess: Option[Double],
                                accuracy: Double = 1.0e-6,
                                maxIterations: Int = 100): Double = ???

  /** Black 1976 implied standard deviation,
   *  i.e. volatility*sqrt(timeToMaturity)
   */
  def blackFormulaImpliedStdDev(payoff: PlainVanillaPayoff,
                                forward: Double,
                                blackPrice: Double,
                                discount: Double = 1.0,
                                displacement: Double = 0.0,
                                guess: Option[Double],
                                accuracy: Double = 1.0e-6,
                                maxIterations: Int = 100): Double = ???


  /** Black 1976 probability of being in the money (in the bond martingale
    * measure), i.e. N(d2).
    * It is a risk-neutral probability, not the real world one.
    * \warning instead of volatility it uses standard deviation,
    *             i.e. volatility*sqrt(timeToMaturity)
    */
  def blackFormulaCashItmProbability(optionType: Option.Type,
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
  def blackFormulaCashItmProbability(payoff: PlainVanillaPayoff,
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
  def blackFormulaStdDevDerivative(strike: Double,
                                    forward: Double,
                                    stdDeviation: Double,
                                    discount: Double = 1.0,
                                    displacement: Double = 0.0): Double = ???

  /*! Black 1976 formula for  derivative with respect to implied vol, this
      is basically the vega, but if you want 1% change multiply by 1%
 */
  def blackFormulaVolDerivative(Real strike,
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
  def blackFormulaStdDevDerivative(
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
  def bachelierBlackFormula(Option::Type optionType,
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
  def bachelierBlackFormula(
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
  def bachelierBlackFormulaImpliedVol(Option::Type optionType,
    Real strike,
    Real forward,
    Real tte,
    Real bachelierPrice,
    Real discount = 1.0);

}
}
