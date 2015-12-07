package org.scalaquant.core

import org.joda.time.LocalDate

package object types {
  type Rate = Double
  type DiscountFactor = Double
  type Spread = Double
  type Volatility = Double
  type Probability = Double
  type YearFraction = Double
  type TimeBasket = Map[LocalDate, Double]
  type Natural = Int
}
