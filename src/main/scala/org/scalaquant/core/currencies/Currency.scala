package org.scalaquant.core.currencies

import org.scalaquant.math.{ NoRounding, Rounding }

trait Currency {
  def definition: CurrencyDefinition

  def ==(other: Currency): Boolean = this.definition.numericCode == other.definition.numericCode

  def !=(other: Currency): Boolean = this.definition.numericCode != other.definition.numericCode
}

object Currency {
  val NullCurrency = new Currency {
    val definition: CurrencyDefinition = CurrencyDefinition("", "", 0, "", "", 0)
  }
}

case class CurrencyDefinition(name: String,
  code: String,
  numericCode: Int,
  symbol: String,
  fractionSymbol: String,
  fractionPerUnit: Int,
  triangulationCurrency: Option[Currency] = None,
  rounding: Rounding = NoRounding())