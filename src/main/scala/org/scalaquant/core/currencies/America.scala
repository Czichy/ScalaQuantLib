package org.scalaquant.core.currencies

object America {

  case class ARSCurrency(definition: CurrencyDefinition = CurrencyDefinition("Argentinian peso", "ARS", 32, "", "", 100))
    extends Currency

  case class BRLCurrency(definition: CurrencyDefinition = CurrencyDefinition("Brazilian real", "BRL", 986, "R$", "", 100))
    extends Currency

  case class CADCurrency(definition: CurrencyDefinition = CurrencyDefinition("Canadian dollar", "CAD", 124, "Can$", "", 100))
    extends Currency

  case class CLPCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Chilean peso", "CLP", 152, "Ch$", "", 100)) extends Currency

  case class COPCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Colombian peso", "COP", 170, "Col$", "", 100)) extends Currency

  case class MXNCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Mexican peso", "MXN", 484, "Mex$", "", 100)) extends Currency

  case class PENCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Peruvian nuevo sol", "PEN", 604, "S/.", "", 100)) extends Currency

  case class PEICurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Peruvian inti", "PEI", 998, "I/.", "", 100)) extends Currency

  case class PEHCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Peruvian sol", "PEH", 999, "S./", "", 100)) extends Currency

  case class TTDCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Trinidad & Tobago dollar", "TTD", 780, "TT$", "", 100)) extends Currency

  case class USDCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("U.S. dollar", "USD", 840, "$", "0xA2", 100)) extends Currency

  case class VEBCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Venezuelan bolivar", "VEB", 862, "Bs", "", 100)) extends Currency

}