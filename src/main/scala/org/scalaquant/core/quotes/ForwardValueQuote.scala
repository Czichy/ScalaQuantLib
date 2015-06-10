package org.scalaquant.core.quotes

import org.joda.time.LocalDate
import org.scalaquant.core.indexes.Index


case class ForwardValueQuote(value: Double) extends ValidQuote{
  override def map(f: Quote.Calculation): Quote = if (isValid) ForwardValueQuote(f(value)) else InvalidQuote
}

object ForwardValueQuote{
  def apply(index: Index, fixingDate: LocalDate): Quote = {
    index.fixing(fixingDate).map(ForwardValueQuote(_)).getOrElse(InvalidQuote)
  }
}