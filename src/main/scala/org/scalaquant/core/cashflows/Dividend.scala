package org.scalaquant.core.cashflows

import java.time.LocalDate

sealed trait Dividend extends CashFlow

final case class FixedDividend(amount: Double, date: LocalDate) extends Dividend

final case class FractionalDividend(rate: Double,
                                    nominal: Option[Double] = None,
                                    underlying: Double,
                                    date: LocalDate)
  extends Dividend {
    def amount = rate * nominal.getOrElse(underlying)
  }


