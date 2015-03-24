package org.scalaquant.core.quotes


case class EurodollarFuturesImpliedStdDevQuote(forward: Quote,
                                          callPrice: Quote,
                                          putPrice: Quote,
                                          strike: Double,
                                          guess: Double = .15,
                                          accuracy:Double = 1.0E-6,
                                          maxIter:Int = 100) extends Quote{
  val value: Double =
}
