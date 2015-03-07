package org.scalaquant.core.instruments

import org.joda.time.{ DateTime, LocalDate }
import org.scalaquant.core.pricingengines.PricingEngine
import rx.lang.scala.{ Subscription, Observer }

/**
 * Created by neo on 2015-02-28.
 */
trait Instrument[T] extends Observer[T] {
  protected var _isCompleted = true
  protected var _NPV: Double = Double.NaN
  protected var _errorEstimate: Double = Double.NaN
  protected var _valuationDate: Option[DateTime] = None
  protected var _additionalResults: Map[String, T] = Map.empty
  protected var _engineSubscription: Option[Subscription] = None

  def NPV: Double = _NPV
  def errorEstimate: Double = _errorEstimate
  def valuationDate: Option[DateTime] = _valuationDate

  def result(tag: String): Option[T] = _additionalResults.get(tag)
  def additionalResults: Map[String, Any] = _additionalResults

  def isExpired: Boolean = this._isCompleted
  def priceEngine_[A](priceEngine: PricingEngine[A, T]): Unit = {
    _engineSubscription = Some(priceEngine.subscribe(this))
    priceEngine.reset()
    priceEngine.arguments.validate()
    priceEngine.calculate()
  }

  protected def setupExpired(): Unit = {
    _NPV = Double.NaN
    _errorEstimate = Double.NaN
    _valuationDate = None
    _additionalResults = Map.empty
    _engineSubscription.map(_.unsubscribe())
  }

  override protected def onCompleted(): Unit = {
    _isCompleted = true
    setupExpired()
  }

}
