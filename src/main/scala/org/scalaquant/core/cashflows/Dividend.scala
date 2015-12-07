package org.scalaquant.core.cashflows

import org.joda.time.LocalDate

sealed trait Dividend extends CashFlow

final case class FixedDividend(override val amount: Double,
                               override val date: LocalDate)
  extends Dividend

final case class FractionalDividend(rate: Double,
                                    nominal: Option[Double] = None,
                                    underlying: Double,
                                    override val date: LocalDate)
  extends Dividend(rate * nominal.getOrElse(underlying), date)

