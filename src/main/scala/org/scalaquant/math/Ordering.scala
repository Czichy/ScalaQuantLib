package org.scalaquant.math

import org.scalaquant.common.Event

object Ordering {
  object Implicits{
    implicit object CashFlowOrdering extends Ordering[Event] {
      override def compare(x: Event, y: Event): Int = x.date compareTo y.date
    }
  }
}
