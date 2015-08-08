package org.scalaquant.common

import org.scalaquant.core.currencies._
import org.scalaquant.math.Calculation._
import org.scalaquant.math.Comparison._

object Money {

  sealed trait ConversionType

  case class BaseCurrencyConversion(base: Currency) extends ConversionType
  case object AutomatedConversion extends ConversionType

  implicit object MoneyRelational extends InEquality[Money] with Equality[Money] with Order[Money]{
    val exchangeRateManager: ExchangeRateManager = DefaultExchangeRateManager //TODO: refactor to DI

    import Currency._
    import org.scalaquant.math.Comparing.ImplicitsOps._

    private def convert(money: Money, target: Currency): Money =
      if (money.currency =/= target)
        exchangeRateManager.lookup(money.currency, target).exchange(money).rounded
      else
        money

    private type Operator = (Double, Double) => Boolean

    private def relationalOperation(x: Money, y: Money)(operator: Operator)(implicit conversionType: ConversionType): Boolean =
      if (x.currency === y.currency)
        operator(x.value, y.value)
      else
        conversionType match {
          case BaseCurrencyConversion(base) => operator(convert(x, base).value, convert(y, base).value)
          case AutomatedConversion => operator(convert(y, x.currency).value, x.value)
        }


    def ==(x: Money, y: Money): Boolean = relationalOperation(x,y)(_ == _)
    def >(x: Money, y: Money): Boolean = relationalOperation(x,y)(_ > _)
    def <(x: Money, y: Money): Boolean = relationalOperation(x,y)(_ < _)
    def !=(x: Money, y: Money): Boolean = relationalOperation(x,y)(_ != _)

  }

  implicit object MoneyArithmetic extends Arithmetic[Money] with ArithmeticWithAnyVal[Money]{

    private type Operator = (Double, Double) => Double
    private def arithmeticOperation(x: Money, y: Money)(operator:Operator): Money = Money(operator.apply(x.value, y.value), x.currency)
    private def arithmeticOperation(x: Double, y: Money)(operator:Operator): Money = Money(operator.apply(x, y.value), y.currency)
    private def arithmeticOperation(x: Money, y: Double)(operator:Operator): Money = Money(operator.apply(x.value, y), x.currency)

    def +(x: Money, y: Money): Money = arithmeticOperation(x,y)(_+_)
    def /(x: Money, y: Money): Money = arithmeticOperation(x,y)(_/_)
    def -(x: Money, y: Money): Money = arithmeticOperation(x,y)(_-_)
    def *(x: Money, y: Money): Money = arithmeticOperation(x,y)(_*_)

    def +(x: I, y: Money): Money = arithmeticOperation(x,y)(_+_)
    def +(x: Money, y: I): Money = arithmeticOperation(x,y)(_+_)
    def /(x: I, y: Money): Money = arithmeticOperation(x,y)(_/_)
    def /(x: Money, y: I): Money = arithmeticOperation(x,y)(_/_)
    def -(x: I, y: Money): Money = arithmeticOperation(x,y)(_-_)
    def -(x: Money, y: I): Money = arithmeticOperation(x,y)(_-_)
    def *(x: I, y: Money): Money = arithmeticOperation(x,y)(_*_)
    def *(x: Money, y: I): Money = arithmeticOperation(x,y)(_*_)
  }
}

case class Money(value: Double, currency: Currency) extends NumberLike[Money] {
  def rounded: Money = Money(currency.definition.rounding(value), currency)
  def unary_ = Money(-this.value, this.currency)
}


