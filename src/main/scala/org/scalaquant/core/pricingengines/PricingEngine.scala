package org.scalaquant.core.pricingengines

import PricingEngine._

trait PricingEngine {
  def results: Results
  def arguments: Arguments
  def calculate(): Unit
}

trait GenericEngine extends PricingEngine {
  protected val _arguments: Arguments
  protected val _result: Results
  def results: Results = _result
  def arguments: Arguments = _arguments
}

object PricingEngine{
  trait Results
  trait Arguments {
    def validated: Boolean
  }
}