package org.scalaquant.core.instruments.equities

import org.scalaquant.core.instruments.{Quote, Instrument}

class Stock extends Instrument[Double] {
  private var _quote: Option[Quote[Double]] = None
  def this(quote: Quote[Double]) {
    this()
    _quote = Some(quote)
    quote.subscribe(onNext, onError)
  }
  override val isExpired: Boolean = false

  def value: Double = Double.NaN
  override def onNext(value: Double): Unit = this._NPV = value
  override def onError(error: Throwable): Unit = this._NPV = Double.NaN
  override def onCompleted(): Unit = this._NPV = Double.NaN

}