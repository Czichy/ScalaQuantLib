package org.scalaquant.core.currencies

object Asia {

  case class BDTCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Bangladesh taka", "BDT", 50, "Bt", "", 100)) extends Currency

  case class CNYCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Chinese yuan", "CNY", 156, "Y", "", 100)) extends Currency

  case class HKDCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Hong Kong dollar", "HKD", 344,
      "HK$", "", 100)) extends Currency

  case class ILSCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Israeli shekel", "ILS", 376,
      "NIS", "", 100)) extends Currency

  case class INRCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Indian rupee", "INR", 356,
      "Rs", "", 100)) extends Currency

  case class IQDCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Iraqi dinar", "IQD", 368,
      "ID", "", 1000)) extends Currency

  case class IRRCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Iranian rial", "IRR", 364,
      "Rls", "", 1)) extends Currency

  case class JPYCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Japanese yen", "JPY", 392, "0xA5", "", 100)) extends Currency

  case class KRWCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("South-Korean won", "KRW", 410, "W", "", 100)) extends Currency

  case class KWDCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Kuwaiti dinar", "KWD", 414, "KD", "", 1000)) extends Currency

  case class NPRCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Nepal rupee", "NPR", 524,
      "NRs", "", 100)) extends Currency

  case class PKRCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Pakistani rupee", "PKR", 586, "Rs", "", 100)) extends Currency

  case class SARCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Saudi riyal", "SAR", 682, "SRls", "", 100)) extends Currency

  case class SGDCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Singapore dollar", "SGD", 702, "S$", "", 100)) extends Currency

  case class THBCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Thai baht", "THB", 764, "Bht", "", 100)) extends Currency

  case class TWDCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Taiwan dollar", "TWD", 901, "NT$", "", 100)) extends Currency
}
