package org.scalaquant.core.instruments.equities

import org.scalaquant.core.instruments.Instrument
import org.scalaquant.core.quotes.Quote

class Stock(quote: Quote) extends Instrument[Double] {

  protected override var _engineSubscription = Some(quote.subscribe(this))
  override val isExpired: Boolean = false

  def value: Double = this._NPV

  override def onNext(value: Double): Unit = this._NPV = value
  override def onError(error: Throwable): Unit = this._NPV = Double.NaN

}