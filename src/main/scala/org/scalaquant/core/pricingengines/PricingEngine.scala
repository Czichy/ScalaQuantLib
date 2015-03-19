package org.scalaquant.core.pricingengines

import rx.lang.scala.Observable
/**
 * Created by neo on 2015-02-28.
 */
import PricingEngine._

trait PricingEngine[T] extends Observable[T] {
  def results: Results
  def arguments: Arguments
  def reset(): Unit = {
    if (arguments.validated) this.calculate()
  }
  def calculate(): Unit
}

trait GenericEngine[T] extends PricingEngine[T] {
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