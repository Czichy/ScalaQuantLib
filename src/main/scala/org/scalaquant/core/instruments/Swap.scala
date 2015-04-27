package org.scalaquant.core.instruments

import org.joda.time.LocalDate
import org.scalaquant.core.cashflows.Leg
import org.scalaquant.core.pricingengines.PricingEngine

/*! The cash flows belonging to the first leg are paid;
            the ones belonging to the second leg are received.
        */
abstract class Swap(val firstLeg: Leg, val secondLeg: Leg, results: Swap.Results ) {
  private var _legs = List((firstLeg, -1.0), (secondLeg, 1.0))
  private var _results = results

  def this(legs: List[(Leg, Double)], results: Swap.Results) = {
    this
    _legs = legs
    _results = results
  }

  def legs: List[(Leg, Double)] = _legs

  def isExpired: Boolean = _legs.flatMap(_._1).forall(_.hasOccurred())
  def resetResult: Swap.Results =  Swap.Results(List.fill(_legs.size)(0.0),
                                                List.fill(_legs.size)(0.0),
                                                List.fill(_legs.size)(0.0),
                                                List.fill(_legs.size)(0.0),
                                                0.0)
  def startDate: LocalDate = {
    require(_legs.nonEmpty, "no legs given")
    _legs.map(_._1)
  }
  def maturityDate: LocalDate

  def legBPS(j: Int): Double = {
    require(j < _legs.size, s"leg# sj doesn't exist!")
    _results.legBPS(j)
  }
  def legNPV(j: Int): Double = {
    require(j < _legs.size, s"leg# sj doesn't exist!")
    _results.legNPV(j)
  }
  def startDiscounts(j: Int): Double = {
    require(j < _legs.size, s"leg# sj doesn't exist!")
    _results.startDiscounts(j)
  }
  def endDiscounts(j: Int): Double = {
    require(j < _legs.size, s"leg# sj doesn't exist!")
    _results.endDiscounts(j)
  }
  def npvDateDiscount: Double = {
    _results.npvDateDiscount
  }

  def leg(j: Int): Leg = {
    require(j < _legs.size, s"leg# sj doesn't exist!")
    _legs(j)._1
  }
}


object Swap {

  case class Arguments(legs: List[(Leg, Boolean)]) extends PricingEngine.Arguments

  case class Results(legNPV: List[Double],
                     legBPS: List[Double],
                     startDiscounts: List[Double],
                     endDiscounts: List[Double],
                     npvDateDiscount: Double) extends PricingEngine.Results

  val InitialResults = Results(Nil,Nil,Nil,Nil,0.0)

  val EmptyArguments = Arguments(Nil)
}