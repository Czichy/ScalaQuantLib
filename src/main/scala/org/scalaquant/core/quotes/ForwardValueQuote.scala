package org.scalaquant.core.quotes

import java.time.LocalDate
import org.scalaquant.core.indexes.Index


case class ForwardValueQuote(value: Double) extends ValidQuote{
  override def map(f: Double => Double): Quote = if (isValid) ForwardValueQuote(f(value)) else InvalidQuote
}

object ForwardValueQuote{
  def apply(index: Index, fixingDate: LocalDate): Quote = {
    ForwardValueQuote(index.fixing(fixingDate))
  }
}