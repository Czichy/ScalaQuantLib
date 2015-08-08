package org.scalaquant.core.currencies

object Europe {
  case class BGLCurrency(definition: CurrencyDefinition = CurrencyDefinition("Bulgarian lev", "BGL", 100, "lv", "", 100)) extends Currency

  case class BYRCurrency(definition: CurrencyDefinition = CurrencyDefinition("Belarussian ruble", "BYR", 974, "BR", "", 1)) extends Currency

  case class CHFCurrency(definition: CurrencyDefinition = CurrencyDefinition("Swiss franc", "CHF", 756, "SwF", "", 100)) extends Currency

  case class CYPCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Cyprus pound", "CYP", 196, "0xA3", "", 100)) extends Currency

  case class CZKCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Czech koruna", "CZK", 203, "Kc", "", 100)) extends Currency

  case class DKKCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Danish krone", "DKK", 208, "Dkr", "", 100)) extends Currency

  case class EEKCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Estonian kroon", "EEK", 233, "KR", "", 100)) extends Currency

  case class EURCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("European Euro", "EUR", 978, "", "", 100)) extends Currency

  case class GBPCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("British pound sterling", "GBP", 826, "0xA3", "p", 100)) extends Currency

  case class HUFCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Hungarian forint", "HUF", 348, "Ft", "", 1)) extends Currency

  case class ISKCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Iceland krona", "ISK", 352, "IKr", "", 100)) extends Currency

  case class LTLCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Lithuanian litas", "LTL", 440, "Lt", "", 100)) extends Currency

  case class LVLCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Latvian lat", "LVL", 428, "Ls", "", 100)) extends Currency

  case class NOKCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Norwegian krone", "NOK", 578, "NKr", "", 100)) extends Currency

  case class PLNCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Polish zloty", "PLN", 985, "zl", "", 100)) extends Currency

  case class ROLCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Romanian leu", "ROL", 642, "L", "", 100)) extends Currency

  case class RONCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Romanian new leu", "RON", 946, "L", "", 100)) extends Currency

  case class SEKCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Swedish krona", "SEK", 752, "kr", "", 100)) extends Currency

  case class SITCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Slovenian tolar", "SIT", 705, "SlT", "", 100)) extends Currency

  case class TRLCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Turkish lira", "TRL", 792, "TL", "", 100)) extends Currency

  case class TRYCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("New Turkish lira", "TRY", 949, "YTL", "", 100)) extends Currency

  case class ATSCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Austrian shilling", "ATS", 40, "", "", 100)) extends Currency

  case class BEFCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Belgian franc", "BEF", 56, "", "", 1)) extends Currency

  case class DEMCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Deutsche mark", "DEM", 276, "DM", "", 100)) extends Currency

  case class ESPCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Spanish peseta", "ESP", 724, "Pta", "", 100)) extends Currency

  case class FIMCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Finnish markka", "FIM", 246, "mk", "", 100)) extends Currency

  case class FRFCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("French franc", "FRF", 250, "", "", 100)) extends Currency

  case class GRDCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Greek drachma", "GRD", 300, "", "", 100)) extends Currency

  case class IEPCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Irish punt", "IEP", 372, "", "", 100)) extends Currency

  case class ITLCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Italian lira", "ITL", 380, "L", "", 1)) extends Currency

  case class LUFCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Luxembourg franc", "LUF", 442, "F", "", 100)) extends Currency

  case class MTLCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Maltese lira", "MTL", 470, "Lm", "", 100)) extends Currency

  case class NLGCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Dutch guilder", "NLG", 528, "f", "", 100)) extends Currency

  case class PTECurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Portuguese escudo", "PTE", 620, "Esc", "", 100)) extends Currency

  case class SKKCurrency(
    definition: CurrencyDefinition = CurrencyDefinition("Slovak koruna", "SKK", 703, "Sk", "", 100)) extends Currency

}
