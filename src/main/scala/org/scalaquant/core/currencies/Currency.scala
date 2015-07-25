package org.scalaquant.core.currencies

import org.scalaquant.math.Comparison.Equality
import org.scalaquant.math.{ NoRounding, Rounding }

trait Currency {

  def definition: CurrencyDefinition
  //override def hashCode = definition.hashCode

}

object Currency {

  val Null = new Currency {
    val definition: CurrencyDefinition = CurrencyDefinition("", "", 0, "", "", 0)
  }

  implicit object CurrencyEquality extends Equality[Currency] {
    def ==(x: Currency, y: Currency): Boolean = x.definition.numericCode == y.definition.numericCode
  }

  implicit object CurrencyEquality extends Equality[Currency] {
    def ==(x: Currency, y: Currency): Boolean = x.definition.numericCode == y.definition.numericCode
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