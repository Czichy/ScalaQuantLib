package org.scalaquant.core.models

import org.scalaquant.core.instruments.options.Option


trait Model

trait AffineModel extends Model{
  def discount(time: Double): Double

  def discountBond(now: Double, maturity: Double, factors: Array): Double

  def discountBondOption(optionType: Option.Type, strike: Double, maturity: Double, bondMaturity: Double): Double


}