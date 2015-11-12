package org.scalaquant.math

import org.scalaquant.math.distributions.GammaFunction
import Constants._
import math._

object Functions {
  type GammaFunction = (Double, Double, Double, Int) => Double

  //val incompleteGammaFunction: GammaFunction = (a, x, accuracy, maxIteration) =>
  def incompleteGammaFunction(a: Double, x: Double, accuracy: Double = 1.0e-13, maxIteration: Int = 100): Double =
  {
    require( a > 0.0, "non-positive a is not allowed" )
    require( x >= 0.0, "negative x not allowed")

    if (x < (a+1.0))
      incompleteGammaFunctionSeriesRepr(a, x, accuracy, maxIteration) // Use the series representation
    else
      1.0 - incompleteGammaFunctionContinuedFractionRepr(a, x, accuracy, maxIteration) // Use the continued fraction representation
  }

  private def incompleteGammaFunctionSeriesRepr(a: Double, x: Double, accuracy: Double = 1.0e-13, maxIteration: Int = 100) = {
    if (x==0.0) {
      0.0
    } else {
      val gln = GammaFunction.logValue(a)
      var ap=a
      var del, sum = 1.0/a
      var n = 0
      var result = Double.NaN

      while(n < maxIteration) {
        ap += 1
        del *= x/ap
        sum += del
        if ( del.abs < sum.abs * accuracy) {
          result = sum * math.exp( -x + a * math.log(x) - gln)
          n = maxIteration
        }
        n += 1
      }

      if(result == Double.NaN) throw new ArithmeticException("accuracy not reached")
      else result
    }
  }

  private def incompleteGammaFunctionContinuedFractionRepr(a: Double, x: Double, accuracy: Double = 1.0e-13, maxIteration: Int = 100): Double = {
    val gln = GammaFunction.logValue(a)
    var b = x+1.0-a
    var c = 1.0/QL_EPSILON
    var d = 1.0/b
    var h = d
    var n = 0
    var result = Double.NaN
    while(n < maxIteration) {
      n += 1
      val an = -n*(n-a)
      b += 2.0
      d = an*d+b
      if (scala.math.abs(d) < QL_EPSILON) d = QL_EPSILON
      c = b+an/c
      if (scala.math.abs(c) < QL_EPSILON) c = QL_EPSILON
      d = 1.0/d
      val del = d*c
      h *= del
      if (math.abs(del-1.0) < accuracy) {
        result = math.exp(-x+a*math.log(x)-gln)*h
        n = maxIteration + 1
      }
    }
    if(result == Double.NaN) throw new ArithmeticException("accuracy not reached")
    else result
  }

  def betaFunction(z: Double, w: Double): Double = {
     math.exp(GammaFunction.logValue(z) + GammaFunction.logValue(w) - GammaFunction.logValue(z+w))
  }

  def betaContinuedFraction(a: Double, b: Double, x: Double, accuracy: Double = 1.0e-13, maxIteration: Int = 100): Double = {
    var aa, del
    var qab = a+b
    var qap = a+1.0
    var qam = a-1.0
    var c = 1.0
    var d = 1.0-qab*x/qap
    if (std::fabs(d) < QL_EPSILON)
      d = QL_EPSILON;
    d = 1.0/d;
    Real result = d;

    var m = 0
    , m2;
    while(m < maxIteration) {
      m = m + 1
      m2=2*m;
      aa=m*(b-m)*x/((qam+m2)*(a+m2));
      d=1.0+aa*d;
      if (abs(d) < QL_EPSILON) d=QL_EPSILON;
      c=1.0+aa/c;
      if (abs(c) < QL_EPSILON) c=QL_EPSILON;
      d=1.0/d;
      result *= d*c;
      aa = -(a+m)*(qab+m)*x/((a+m2)*(qap+m2));
      d=1.0+aa*d;
      if (abs(d) < QL_EPSILON) d=QL_EPSILON;
      c=1.0+aa/c;
      if (abs(c) < QL_EPSILON) c=QL_EPSILON;
      d=1.0/d;
      del=d*c;
      result *= del;
      if (abs(del - 1.0) < accuracy)
        return result

    }
    QL_FAIL("a or b too big, or maxIteration too small in betacf");
  }

  //! Incomplete Beta function
  /*! Incomplete Beta function

      The implementation of the algorithm was inspired by
      "Numerical Recipes in C", 2nd edition,
      Press, Teukolsky, Vetterling, Flannery, chapter 6
  */
  def incompleteBetaFunction(a: Double, b: Double, x: Double, accuracy: Double = 1.0e-16, maxIteration: Int = 100): Double = {
    require(a > 0.0, "a must be greater than zero")
    require(b > 0.0, "b must be greater than zero")


    if (x == 0.0) {
      0.0
    } else if (x == 1.0) {
      1.0
    } else {
      require(x > 0.0 && x < 1.0, "x must be in [0,1]")

      val result = math.exp(GammaFunction.logValue(a + b) -
        GammaFunction.logValue(a) - GammaFunction.logValue(b) +
        a * Math.log(x) + b * Math.log(1.0 - x))

      if (x < (a + 1.0) / (a + b + 2.0))
         result * betaContinuedFraction(a, b, x, accuracy, maxIteration) / a
      else
         1.0 - result * betaContinuedFraction(b, a, 1.0 - x, accuracy, maxIteration) / b
    }

  }

}
