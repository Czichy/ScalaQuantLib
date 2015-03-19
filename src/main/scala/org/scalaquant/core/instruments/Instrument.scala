package org.scalaquant.core.instruments

import org.joda.time. LocalDate
import org.scalaquant.core.pricingengines.PricingEngine
import rx.lang.scala.{ Subscription, Observer }

/**
 * Created by neo on 2015-02-28.
 */
trait Instrument[T] extends Observer[T] {
  protected var _isCompleted = true
  protected var _NPV: Double = 0.0
  protected var _errorEstimate: Double = 0.0
  protected var _valuationDate: Option[LocalDate] = None
  protected var _additionalResults: Map[String, T] = Map.empty
  protected var _engineSubscription: Option[Subscription] = None

  def NPV: Double = _NPV
  def errorEstimate: Double = _errorEstimate
  def valuationDate: Option[LocalDate] = _valuationDate

  def result(tag: String): Option[T] = _additionalResults.get(tag)
  def additionalResults: Map[String, Any] = _additionalResults

  def isExpired: Boolean = this._isCompleted

  def priceEngine_(priceEngine: PricingEngine[T]): Unit = {
    _isCompleted = false
    setupExpired()
    _engineSubscription = Some(priceEngine.subscribe(this))
    priceEngine.reset()
  }

  protected def setupExpired(): Unit = {
    _NPV = Double.NaN
    _errorEstimate = Double.NaN
    _valuationDate = None
    _additionalResults = Map.empty
    _engineSubscription.map(_.unsubscribe())
  }

  override def onCompleted(): Unit = {
    _isCompleted = true
  }
}


