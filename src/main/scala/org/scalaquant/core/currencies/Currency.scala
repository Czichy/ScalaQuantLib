package org.scalaquant.core.currencies

import org.scalaquant.math.Comparison._
import org.scalaquant.math.{ NoRounding, Rounding }

case class Currency(name: String,
                    code: String,
                    numericCode: Int,
                    symbol: String,
                    fractionSymbol: String,
                    fractionPerUnit: Int,
                    triangulationCurrency: Option[Currency] = None,
                    rounding: Rounding = NoRounding())

object Currency {

  val Null = new Currency("", "", 0, "", "", 0)

  implicit object CurrencyEquality extends Equality[Currency] {
    def ==(x: Currency, y: Currency): Boolean = x.numericCode == y.numericCode
  }

}
