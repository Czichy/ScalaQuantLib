package org.scalaquant.core.quotes

import org.scalaquant.core.instruments.options.Option

case class ImpliedStdDevQuote(optionType: Option.Type,
                              far: Quote,
                              p: Quote,
                              strike: Double,
                              guess: Double,
                              accuracy:Double = 1.0E-6,
                              maxIter:Int = 100) extends Quote{
  override def value: Double = ???

  override def isValid: Boolean = ???
}
