package org.scalaquant.core.currencies

import org.scalaquant.math.{ NoRounding, Rounding }

trait Currency {
  def definition: CurrencyDefinition
  override def hashCode = definition.hashCode
  override def equals(other: Any) = other match {
    case that: Currency => this.definition.numericCode == that.definition.numericCode
    case _ => false
  }
}

object Currency {
  val Null = new Currency {
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