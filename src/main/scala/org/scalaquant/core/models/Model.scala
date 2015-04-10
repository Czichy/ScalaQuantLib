package org.scalaquant.core.models

import org.scalaquant.core.instruments.options.Option
import org.scalaquant.core.termstructures.YieldTermStructure


trait Model

trait AffineModel extends Model{
  def discount(time: Double): Double

  def discountBond(now: Double, maturity: Double, factors: Array): Double

  def discountBondOption(optionType: Option.Type, strike: Double, maturity: Double, bondMaturity: Double): Double
}

abstract class TermStructureConsistentModel(val termStructure: YieldTermStructure) extends Model

abstract class CalibratedModel