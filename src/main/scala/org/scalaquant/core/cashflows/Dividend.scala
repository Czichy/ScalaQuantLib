package org.scalaquant.core.cashflows

import org.joda.time.LocalDate


sealed trait Dividend extends CashFlow

case class FixedDividend(override val amount: Double, override val date: LocalDate) extends Dividend

case class FractionalDividend(rate: Double, nominal: Double, underlying: Double, override val date: LocalDate) extends Dividend

