package org.scalaquant.core.quotes

import org.joda.time.LocalDate
import org.scalaquant.core.indexes.Index

/**
 * Created by neo on 2015-04-26.
 */
case class ForwardValueQuote(value: Double) extends ValidQuote{
  override def map(f: (Double) => Double): Quote = if (isValid) ForwardValueQuote(f(value)) else InvalidQuote
}

object ForwardValueQuote{
  def apply(index: Index, fixingDate: LocalDate): Quote = {
    index.fixing(fixingDate).map(ForwardValueQuote(_)).getOrElse(InvalidQuote)
  }
}