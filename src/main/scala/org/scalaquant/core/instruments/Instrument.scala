package org.scalaquant.core.instruments

import org.joda.time. LocalDate
import org.scalaquant.core.pricingengines.PricingEngine
import rx.lang.scala.{ Subscription, Observer }

trait Instrument[T] {

  def NPV: Double
  def errorEstimate: Double
  def valuationDate: Option[LocalDate]

  def result(tag: String): T
  def additionalResults: Map[String, T]

  def isExpired: Boolean

}

object Instrument{

  trait Results[T] extends PricingEngine.Results{
    def value: Double
    def errorEstimate: Double
    def valuationDate: LocalDate
    def additionalResults: Map[String, T]
  }

}