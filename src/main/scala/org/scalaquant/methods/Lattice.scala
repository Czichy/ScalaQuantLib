package org.scalaquant.methods

import org.scalaquant.common.{DiscretizedAsset, TimeGrid}


abstract class Lattice(val timeGrid: TimeGrid) {

  def initialize(time: Double): DiscretizedAsset

  def rollback(origin: DiscretizedAsset, to: Double): DiscretizedAsset

  def partialRollback(origin: DiscretizedAsset, to: Double): DiscretizedAsset

  def presentValue(asset: DiscretizedAsset): Double

}


class TreeLattice(override val timeGrid: TimeGrid, val size: Int) extends Lattice(timeGrid) {
  require(size > 0, "there is no zeronomial lattice!")
  type T <: {
    def discount(i: Int, index: Int): Double
    def descendant(i: Int, index: Int, branch: Int): Double
    def probability(i: Int, index: Int, branch: Int): Double
  }
  protected val impl: T

  private var _statePrices = Vector(Array(1.0))
  private val _statePricesLimit = 0

  protected def computeStatePrices(until: Int): Vector[Array[Double]] = {
    (_statePricesLimit to until).map((Array.fill(n)(0.0)).map)
  }

  override def initialize(time: Double): DiscretizedAsset = ???

  override def partialRollback(origin: DiscretizedAsset, to: Double): DiscretizedAsset = {
    List(12,2).distinct
  }

  override def rollback(origin: DiscretizedAsset, to: Double): DiscretizedAsset = ???

  override def presentValue(asset: DiscretizedAsset): Double = ???

  def statePrices(i: Int) =
}