package org.scalaquant.core.currencies

import org.scalaquant.math.Comparison._
import org.scalaquant.math.{ NoRounding, Rounding }

abstract class Currency {

  val name: String = definition.name
  val code: String = definition.code
  val numericCode: Int = definition.numericCode
  val symbol: String = definition.symbol
  val fractionSymbol: String = definition.fractionSymbol
  val fractionPerUnit: Int = definition.fractionPerUnit
  val triangulationCurrency: Option[Currency] = definition.triangulationCurrency
  val rounding: Rounding = definition.rounding
  def definition: CurrencyDefinition
}

object Currency {

  val Null = new Currency {
    val definition: CurrencyDefinition = CurrencyDefinition("", "", 0, "", "", 0)
  }

  implicit object CurrencyEquality extends Equality[Currency] {
    def ==(x: Currency, y: Currency): Boolean = x.numericCode == y.numericCode
  }

  implicit object CurrencyInEquality extends InEquality[Currency] {
    def !=(x: Currency, y: Currency): Boolean = x.numericCode != y.numericCode
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