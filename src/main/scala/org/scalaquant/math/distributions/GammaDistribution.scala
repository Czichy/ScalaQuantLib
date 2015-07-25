package org.scalaquant.math.distributions

import org.scalaquant.math.Constants._
class GammaDistribution {

}

object GammaFunction{

  private val C = List(76.18009172947146,
                  -86.50532032941677,
                   24.01409824083091,
                  -1.231739572450155,
                  0.1208650973866179e-2,
                  -0.5395239384953e-5)

  def logValue(x: Double): Double = {
    require( x > 0.0, "positive argument required")

    val temp = (x + 5.5) - (x + 0.5) * Math.log(x + 5.5)

    val ser = C.zipWithIndex.map(c => c._1 / (x + (c._2 + 1.0))).sum + 1.000000000190015

    -temp + Math.log(2.5066282746310005 * ser / x)
  }

  def value(x: Double): Double = {
    if (x >= 1.0) {
      Math.exp(logValue(x))
    } else {
      if (x > -20.0) {
        // \Gamma(x) = \frac{\Gamma(x+1)}{x}
         value(x+1.0)/x
      } else {
        // \Gamma(-x) = -\frac{\pi}{\Gamma(x)\sin(\pi x) x}
         -M_PI / ( value(-x) * x * Math.sin(M_PI * x) )
      }
    }
  }
}