package org.scalaquant.core.instruments

import org.joda.time. LocalDate
import org.scalaquant.core.pricingengines.PricingEngine
import rx.lang.scala.{ Subscription, Observer }

trait Instrument[T] {
//  protected var _isCompleted = true
//  protected var _NPV: Double = 0.0
//  protected var _errorEstimate: Double = 0.0
//  protected var _valuationDate: Option[LocalDate] = None
//  protected var _additionalResults: Map[String, T] = Map.empty
//  protected var _engineSubscription: Option[Subscription] = None

  def NPV: Double
  def errorEstimate: Double
  def valuationDate: Option[LocalDate]

  def result(tag: String): Option[T]
  def additionalResults: Map[String, Any]

  def isExpired: Boolean

//  def priceEngine_(priceEngine: PricingEngine[T]): Unit = {
//    _isCompleted = false
//    setupExpired()
//    _engineSubscription = Some(priceEngine.subscribe(this))
//    priceEngine.reset()
//  }

//  protected def setupExpired(): Unit = {
//    _NPV = Double.NaN
//    _errorEstimate = Double.NaN
//    _valuationDate = None
//    _additionalResults = Map.empty
//    _engineSubscription.map(_.unsubscribe())
//  }
//
//  override def onCompleted(): Unit = {
//    _isCompleted = true
//  }
}


