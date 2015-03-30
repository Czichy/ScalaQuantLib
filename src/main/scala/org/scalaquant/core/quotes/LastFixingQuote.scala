package org.scalaquant.core.quotes

import org.joda.time.LocalDate
import org.scalaquant.core.indexes.Index

case class LastFixingQuote(index: Index) extends Quote {
  val isValid: Boolean = index.timeSeries.nonEmpty
  val referenceDate: Option[LocalDate] = index.timeSeries.lastDate
  val value: Double = referenceDate.flatMap(index.fixing(_)).getOrElse(Double.NaN)
}
