package org.scalaquant.math

import org.scalaquant.math.distributions.GammaFunction

object Factorial {

//  private val firstFactorials: Array[Double] = Array(
//    1.0,                                   1.0,
//    2.0,                                   6.0,
//    24.0,                                 120.0,
//    720.0,                                5040.0,
//    40320.0,                              362880.0,
//    3628800.0,                            39916800.0,
//    479001600.0,                          6227020800.0,
//    87178291200.0,                       1307674368000.0,
//    20922789888000.0,                     355687428096000.0,
//    6402373705728000.0,                  121645100408832000.0,
//    2432902008176640000.0,                51090942171709440000.0,
//    1124000727777607680000.0,             25852016738884976640000.0,
//    620448401733239439360000.0,          15511210043330985984000000.0,
//    403291461126605635584000000.0,       10888869450418352160768000000.0)
//
//  private val tabulated = firstFactorials.length

//  def get(n: Int): Double = if (n<=tabulated) firstFactorials(n) else math.exp(GammaFunction.logValue(n+1))
//
//  def ln(n: Int): Double = if (n<=tabulated) math.log(firstFactorials(n)) else GammaFunction.logValue(n+1)
  private val N: Stream[Double] = 1.0 #:: N.map(_ + 1.0)
  private val fibs: Stream[Double] =  1.0 #:: fibs.zip(N).map { n => n._1 * n._2 }

  def get(n: Int): Double = fibs(n)
  def ln(n: Int): Double = math.log(fibs(n))

}