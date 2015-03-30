package org.scalaquant.core.common

import org.scalaquant.methods.Lattice

abstract class DiscretizedAsset(val time: Double, val values: List[Double], method: Lattice) {
  def presentValue
  def rollback

  def preAdjustValues
  def postAdjustValues
  def mandatoryTimes: Vector[Double]

  def isOnTime(t: Double)
}
