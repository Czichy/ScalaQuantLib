package org.scalaquant.math.distributions

import org.scalaquant.math.Constants._
import org.scalaquant.math.Comparison._
import org.scalaquant.math.ErrorFunction

sealed abstract class AbstractNormalDistribution(average: Double, sigma: Double) extends (Double => Double){
  require(sigma>0.0, s"sigma must be greater than 0.0 ($sigma not allowed)")
}

class NormalDistribution(average: Double = 0.0, sigma: Double = 1.0) extends AbstractNormalDistribution(average, sigma) {

  private val normalizationFactor = M_SQRT_2 * M_1_SQRTPI / sigma
  private val derNormalizationFactor  = sigma * sigma
  private val denominator = 2.0 * derNormalizationFactor

  def apply(x: Double): Double = {
    val deltaX = x - average
    val exponent = -(deltaX * deltaX) / denominator
    if (exponent <= -690.0) 0.0 else normalizationFactor * Math.exp(exponent) // exp(x) < 1.0e-300 anyway
  }

  def derivative(x: Double): Double = (this.apply(x) * (average - x)) / derNormalizationFactor

}


case class CumulativeNormalDistribution(average: Double = 0.0, sigma: Double = 1.0) extends AbstractNormalDistribution(average, sigma) {

  private val gaussian = new NormalDistribution()

  def apply(z: Double): Double = {
    val _z = (z - average) / sigma

    val result = 0.5 * ( 1.0 + ErrorFunction( _z * M_SQRT_2 ))

    if (result <= 1e-8) {//todo: investigate the threshold level
      // Asymptotic expansion for very negative z following (26.2.12)
      // on page 408 in M. Abramowitz and A. Stegun,
      // Pocketbook of Mathematical Functions, ISBN 3-87144818-4.
      var (sum, zsqr, i, g) = (1.0, _z * _z, 1.0, 1.0)
      var (x, y) =(0.0, 0.0)
      var (a, lasta) = (Double.MaxValue, 0.0)

      do {
        lasta=a
        x = (4.0*i-3.0)/zsqr
        y = x * ((4.0*i-1)/zsqr)
        a = g * (x-y)
        sum = sum - a
        g = g * y
        i = i + 1
        a = Math.abs(a)
      } while ((lasta > a) && (a >= Math.abs(sum * QL_EPSILON)))

      -gaussian.apply(_z) / _z * sum
    } else {
      result
    }
  }

  def derivative(x: Double): Double = gaussian.apply((x - average) / sigma) / sigma
}


class InverseCumulativeNormalDistribution(average: Double = 0.0, sigma: Double = 1.0) extends AbstractNormalDistribution(average, sigma) {

  def apply(x: Double): Double = average + sigma * InverseCumulativeNormalDistribution.standardValue(x)
}

object InverseCumulativeNormalDistribution{

  private val A = List(-3.969683028665376e+01,
                        2.209460984245205e+02,
                        -2.759285104469687e+02,
                        1.383577518672690e+02,
                        -3.066479806614716e+01,
                        2.506628277459239e+00)

  private val B = List(-5.447609879822406e+01,
                      1.615858368580409e+02,
                      -1.556989798598866e+02,
                      6.680131188771972e+01,
                      -1.328068155288572e+01, 1.0)

  private val C = List(-7.784894002430293e-03,
                        -3.223964580411365e-01,
                        -2.400758277161838e+00,
                        -2.549732539343734e+00,
                        4.374664141464968e+00,
                        2.938163982698783e+00)

  private val D = List( 7.784695709041462e-03,
                        3.224671290700398e-01,
                        2.445134137142996e+00,
                        3.754408661907416e+00, 1.0)

  private val x_low_ = 0.02425
  private val x_high_ = 1.0 - x_low_

  private def tailValue(x: Double): Double = {
    if (x <= 0.0 || x >= 1.0) {
      // try to recover if due to numerical error
      if (x ~= 1.0) {
         Double.MaxValue // largest value available
      } else if (x.abs < QL_EPSILON) {
         Double.MinValue // largest negative value available
      } else {
//        QL_FAIL("InverseCumulativeNormal(" << x
//          << ") undefined: must be 0 < x < 1");
        Double.NaN
      }
    } else {

      val z = Math.sqrt( -2.0 * Math.log(if (x < x_low_) x else 1.0 - x))
      C.reduce(_ * z + _) / D.reduce(_ * z + _)
   }
  }

  def standardValue(x: Double):Double = {

    if (x < x_low_ || x_high_ < x) {
      tailValue(x)
    } else {
      val z = x - 0.5
      val r = z * z
      A.reduce(_ * r + _) * z / B.reduce(_ * r + _)
    }

//
//    // The relative error of the approximation has absolute value less
//    // than 1.15e-9.  One iteration of Halley's rational method (third
//    // order) gives full machine precision.
//    // #define REFINE_TO_FULL_MACHINE_PRECISION_USING_HALLEYS_METHOD
//    #ifdef REFINE_TO_FULL_MACHINE_PRECISION_USING_HALLEYS_METHOD
//      // error (f_(z) - x) divided by the cumulative's derivative
//      const Real r = (f_(z) - x) * M_SQRT2 * M_SQRTPI * exp(0.5 * z*z);
//    //  Halley's method
//    z -= r/(1+0.5*z*r);
//    #endif

  }
}

class MoroInverseCumulativeNormal(average: Double = 0.0, sigma: Double = 1.0) extends AbstractNormalDistribution(average, sigma) {
  def apply(x: Double): Double = ???
}

class MaddockCumulativeNormal(average: Double = 0.0, sigma: Double = 1.0) extends AbstractNormalDistribution(average, sigma) {
  def apply(x: Double): Double = ???
}