package org.scalaquant.math

object Factorial {

  private val N: Stream[Double] = 1.0 #:: N.map(_ + 1.0)
  private val fibs: Stream[Double] =  1.0 #:: fibs.zip(N).map { n => n._1 * n._2 }

  def get(n: Int): Double = fibs(n)
  def ln(n: Int): Double = math.log(fibs(n))

}
