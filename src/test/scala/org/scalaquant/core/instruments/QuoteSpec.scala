package org.scalaquant.core.instruments

import org.scalaquant.core.quotes.{DerivedQuote, SimpleQuote}
import org.scalatest.{Matchers, FlatSpec}
import org.scalaquant.core.quotes.Quote.Calculation

class QuoteSpec extends FlatSpec with Matchers {

  private def add(x:Double, y:Double)  = x + y
  private def mul(x:Double, y:Double)  = x * y
  private def sub(x:Double, y:Double)  = x - y

  private val mul10: Calculation = mul(_, 10)
  private val add10: Calculation = add(_, 10)
  private val sub10: Calculation = sub(_, 10)


  "Derived quotes" should "be able to derived from SimpleQuote" in {

    val simpleQuote = SimpleQuote(0.0)

    val calculations = List[Calculation](add10, mul10, sub10)

    val derivedQuotes = calculations map { DerivedQuote(simpleQuote, _) }

    derivedQuotes shouldEqual List(DerivedQuote(10.0), DerivedQuote(0.0), DerivedQuote(-10.0))

  }

//  "Composite Quote" should "be able to composited from SimpleQuote" in {
//
//  }
}
