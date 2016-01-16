package org.scalaquant.core.instruments.equities

import java.time.LocalDate
import org.scalaquant.core.instruments.{NoExpiration, Instrument}
import org.scalaquant.core.quotes.Quote

case class Stock(quote: Quote) extends Instrument with NoExpiration {
 def valuationDate = LocalDate.now()
}