package org.scalaquant.core.models

import org.scalaquant.core.instruments.options.Option


class Model {

}

trait AffineModel{
  def discount(time: Double): DiscountFactor

  def discountBond(now: Double, maturity: Double, factors: Array): Double

  def discountBondOption(optionType: Option.Type, strike: Double, maturity: Double, bondMaturity: Double): Double


}