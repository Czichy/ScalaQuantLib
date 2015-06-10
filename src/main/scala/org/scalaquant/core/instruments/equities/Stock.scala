package org.scalaquant.core.instruments.equities

import org.scalaquant.core.instruments.Instrument
import org.scalaquant.core.quotes.Quote

class Stock(quote: Quote) extends Instrument[Double] {

  override val isExpired: Boolean = false

  val value: Double = NPV

}