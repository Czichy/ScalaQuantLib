package org.scalaquant.core.currencies

/**
 * Created by neo on 2015-02-28.
 */
object Oceania {
  case class AUDCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Australian dollar", "AUD", 36,
      "A$", "", 100)) extends Currency

  case class NZDCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("New Zealand dollar", "NZD", 554,
      "NZ$", "", 100)) extends Currency
}
