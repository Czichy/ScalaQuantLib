package org.scalaquant.math

import org.scalaquant.math.distributions.CumulativeNormalDistribution

import math._

package object copulas {

  private abstract class Copula(theta: Double) extends ((Double, Double) => Double){
    protected def checkXandY(x: Double,y: Double) = {
      require(x >= 0.0 && x <=1.0 , s"1st argument ($x) must be in [0,1]")
      require(y >= 0.0 && y <=1.0 , s"2nd argument ($y) must be in [0,1]")
    }

  }

  final class AliMikhailHaqCopula(theta: Double) extends Copula(theta){
    require( theta >= -1.0 && theta <= 1.0, s"theta ($theta) must be in [-1,1]")

    def apply(x: Double,y: Double): Double = {
      checkXandY(x,y)
      (x*y)/(1.0-theta*(1.0-x)*(1.0-y))
    }
  }

  final class ClaytonCopula(theta: Double) extends Copula(theta){
    require(theta >= -1.0, s"theta ($theta) must be greater or equal to -1")

    require(theta != 0.0, s"theta ($theta) must be different from 0")
    def apply(x: Double,y: Double): Double = {
      checkXandY(x,y)
      max( pow( pow(x,-theta)+pow(y,-theta)-1.0  , -1.0/theta) , 0.0)
    }
  }

  final class FarlieGumbelMorgensternCopula(theta: Double) extends Copula(theta){
    require( theta >= -1.0 && theta <= 1.0, s"theta ($theta) must be in [-1,1]")

    def apply(x: Double,y: Double): Double = {
      checkXandY(x,y)
      x*y + theta*x*y*(1.0 - x)*(1.0 - y)
    }
  }

  final class FrankCopula(theta: Double) extends Copula(theta){
    require(theta != 0.0, s"theta ($theta) must be different from 0")

    def apply(x: Double,y: Double): Double = {
      checkXandY(x,y)
      -1.0/theta  *  log(1 + (exp(-theta*x) -1) * (exp(-theta*y) -1) / (exp(-theta)- 1)   )
    }
  }

  final class GalambosCopula(theta: Double) extends Copula(theta){
    require(theta >= 0.0, "theta ($theta) must be greater or equal to 0")

    def apply(x: Double,y: Double): Double = {
      checkXandY(x,y)
      x*y*exp(pow(pow(-log(x),-theta)+pow(-log(y),-theta),-1/theta))
    }
  }

//  case class GaussianCopula(private val rho: Double) extends Copula(rho){
//    require( rho >= -1.0 && rho <= 1.0, s"rho ($rho) must be in [-1,1]")
//    def apply(x: Double,y: Double): Double = {
//      checkXandY(x,y)
//      x*y*exp(pow(pow(-log(x),-theta)+pow(-log(y),-theta),-1/theta))
//    }
//  }

  final class GumbelCopula(theta: Double) extends Copula(theta){
    require(theta >= 1.0, s"theta ($theta) must be greater or equal to 1")

    def apply(x: Double,y: Double): Double = {
      checkXandY(x,y)
      exp(-pow( pow( -log(x), theta)+pow( -log(y), theta),1/theta))
    }
  }

  final class HuslerReissCopula(theta: Double) extends Copula(theta){
    require(theta >= 0.0, s"theta ($theta) must be greater or equal to 0")
    private val cumNormal = CumulativeNormalDistribution()

    def apply(x: Double,y: Double): Double = {
      checkXandY(x,y)
      pow(x,cumNormal(1.0/theta+0.5*theta*log(-log(x) / -log(y))))*pow(y,cumNormal(1.0/theta+0.5*theta*log(-log(y) / -log(x))))
    }
  }

  final class IndependentCopula(theta: Double = 0.0) extends Copula(theta){

    def apply(x: Double,y: Double): Double = {
      checkXandY(x,y)
      x * y
    }
  }

  final class MarshallOlkinCopula(a1: Double, a2: Double)extends Copula(0){
    require(a1 >= 0.0, s"1st parameter ($a1) must be non-negative")
    require(a2 >= 0.0, s"2nd parameter ($a2) must be non-negative")
    def apply(x: Double,y: Double): Double = {
      checkXandY(x,y)
      min(y * pow(x, 1.0-a1),  x * pow(y, 1.0-a2))
    }
  }

  final class MaxCopula(theta: Double = 0.0) extends Copula(theta){

    def apply(x: Double,y: Double): Double = {
      checkXandY(x,y)
      min(x,y)
    }
  }

  final class MinCopula(theta: Double = 0.0) extends Copula(theta){

    def apply(x: Double,y: Double): Double = {
      checkXandY(x,y)
      max(x+y-1.0, 0.0)
    }
  }

  final class PlackettCopula(theta: Double) extends Copula(theta){
    require(theta >= 0.0, s"theta ($theta) must be greater or equal to 0")
    require(theta != 1.0, s"theta ($theta) must be different from 1")

    def apply(x: Double,y: Double): Double = {
      checkXandY(x,y)
      ((1.0+(theta-1.0)*(x+y))-sqrt(pow(1.0+(theta-1.0)*(x+y),2.0)-4.0*x*y*theta*(theta-1.0)))/(2.0*(theta-1.0))
    }
  }
}
