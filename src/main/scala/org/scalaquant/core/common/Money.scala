package org.scalaquant.core.common
import org.scalaquant.core.currencies.{ DefaultExchangeRateManager, Currency }

/**
 * Created by neo on 2015-02-28.
 */

case class Money(value: Double, currency: Currency) {
  def rounded: Money = Money(currency.definition.rounding.apply(value), currency)
}

object Money {
  trait ConversionType

  case class BaseCurrencyConversion(base: Currency) extends ConversionType
  case object AutomatedConversion extends ConversionType
  private def convert(money: Money, target: Currency): Money = {
    if (money.currency != target) {
      val exRate = DefaultExchangeRateManager.lookup(money.currency, target)
      exRate.exchange(money).rounded
    } else {
      money
    }
  }
  implicit class MoneyOperation(val money: Money) extends AnyVal {

    def unary_ = Money(-money.value, money.currency)
    def *(other: Double): Money = Money(money.value * other, money.currency)
    def /(other: Double): Money = Money(money.value / other, money.currency)

    def +(other: Money)(implicit conversionType: ConversionType): Money = {
      if (money.currency == other.currency) {
        Money(money.value + other.value, money.currency)
      } else {
        conversionType match {
          case BaseCurrencyConversion(base) => Money(convert(money, base).value + convert(other, base).value, base)
          case AutomatedConversion => Money(convert(other, money.currency).value + money.value, money.currency)
        }
      }
    }
    def -(other: Money)(implicit conversionType: ConversionType): Money = {
      if (money.currency == other.currency) {
        Money(money.value - other.value, money.currency)
      } else {
        conversionType match {
          case BaseCurrencyConversion(base) => Money(convert(money, base).value - convert(other, base).value, base)
          case AutomatedConversion => Money(convert(other, money.currency).value - money.value, money.currency)
        }
      }
    }
    def *(other: Money)(implicit conversionType: ConversionType): Money = {
      if (money.currency == other.currency) {
        Money(money.value * other.value, money.currency)
      } else {
        conversionType match {
          case BaseCurrencyConversion(base) => Money(convert(money, base).value * convert(other, base).value, base)
          case AutomatedConversion => Money(convert(other, money.currency).value * money.value, money.currency)
        }
      }
    }
    def /(other: Money)(implicit conversionType: ConversionType): Money = {
      if (money.currency == other.currency) {
        Money(money.value / other.value, money.currency)
      } else {
        conversionType match {
          case BaseCurrencyConversion(base) => Money(convert(money, base).value / convert(other, base).value, base)
          case AutomatedConversion => Money(convert(other, money.currency).value / money.value, money.currency)
        }
      }
    }
    def ==(other: Money)(implicit conversionType: ConversionType): Boolean = {
      if (money.currency == other.currency) {
        money.value == other.value
      } else {
        conversionType match {
          case BaseCurrencyConversion(base) => convert(money, base).value == convert(other, base).value
          case AutomatedConversion => convert(other, money.currency).value == money.value
        }
      }
    }
    def !=(other: Money)(implicit conversionType: ConversionType): Boolean = {
      if (money.currency == other.currency) {
        money.value != other.value
      } else {
        conversionType match {
          case BaseCurrencyConversion(base) => convert(money, base).value != convert(other, base).value
          case AutomatedConversion => convert(other, money.currency).value != money.value
        }
      }
    }
    def <(other: Money)(implicit conversionType: ConversionType): Boolean = {
      if (money.currency == other.currency) {
        money.value < other.value
      } else {
        conversionType match {
          case BaseCurrencyConversion(base) => convert(money, base).value < convert(other, base).value
          case AutomatedConversion => convert(other, money.currency).value < money.value
        }
      }
    }
    def <=(other: Money)(implicit conversionType: ConversionType): Boolean = {
      if (money.currency == other.currency) {
        money.value <= other.value
      } else {
        conversionType match {
          case BaseCurrencyConversion(base) => convert(money, base).value <= convert(other, base).value
          case AutomatedConversion => convert(other, money.currency).value <= money.value
        }
      }
    }
    def >(other: Money)(implicit conversionType: ConversionType): Boolean = {
      if (money.currency == other.currency) {
        money.value > other.value
      } else {
        conversionType match {
          case BaseCurrencyConversion(base) => convert(money, base).value > convert(other, base).value
          case AutomatedConversion => convert(other, money.currency).value > money.value
        }
      }
    }
    def >=(other: Money)(implicit conversionType: ConversionType): Boolean = {
      if (money.currency == other.currency) {
        money.value >= other.value
      } else {
        conversionType match {
          case BaseCurrencyConversion(base) => convert(money, base).value >= convert(other, base).value
          case AutomatedConversion => convert(other, money.currency).value >= money.value
        }
      }
    }

    def ~=(other: Money, precision: Double)(implicit conversionType: ConversionType): Boolean = {
      if (money.currency == other.currency) {
        (money.value - other.value).abs < precision
      } else {
        conversionType match {
          case BaseCurrencyConversion(base) => (convert(money, base).value - convert(other, base).value).abs < precision
          case AutomatedConversion => (convert(other, money.currency).value - money.value).abs < precision
        }
      }
    }
  }
}