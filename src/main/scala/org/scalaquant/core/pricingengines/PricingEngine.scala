package org.scalaquant.core.pricingengines

import rx.lang.scala.Observable
/**
 * Created by neo on 2015-02-28.
 */
trait PricingEngine[A, R] extends Observable[R] {
  trait Results {
    def reset(): Unit
  }
  trait Arguments {
    def validate(): Unit
  }
  def results: Results
  def arguments: Arguments
  def reset(): Unit
  def calculate(): Unit
}

trait GenericEngine[A, R] extends PricingEngine[A, R] {
  protected var _arguments: Arguments
  protected var _result: Results

}