package org.scalaquant.core.pricingengines

import PricingEngine._
import org.scalaquant.core.models.Model

trait PricingEngine[-A, +R] {
  def calculate(a: A): R
}

trait GenericEngine extends PricingEngine[Arguments, Results] {
  def results: Results
  def arguments: Arguments
}

abstract class GenericModelEngine(val model: Model) extends GenericEngine

object PricingEngine {
  trait Results
  trait Arguments

  //def validate(a: Arguments): Validation[]
  //def
}