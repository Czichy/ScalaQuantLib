package org.scalaquant.core.quotes

import org.scalaquant.core.indexes.Index

case class LastFixingQuote(index: Index, value: Double) extends ValidQuote {
  def map(f: Double => Double): Quote = if (isValid) LastFixingQuote(index, f(value)) else InvalidQuote
}

object LastFixingQuote{
  def apply(index: Index): Quote = {
    if (index.timeSeries.nonEmpty) {
      LastFixingQuote(index, index.fixing(index.timeSeries.lastDate))
    } else {
      InvalidQuote
    }
  }
}