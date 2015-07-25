package org.scalaquant.core.instruments

import org.joda.time.LocalDate

abstract class Instrument {

 // def NPV: Double
  def valuationDate: LocalDate
  def isExpired: Boolean

  //  def errorEstimate: Double
  //  def result(tag: String): T
  //  def additionalResults: Map[String, T]

}

trait ExpirationDate {
  def expirationDate: LocalDate
  def isExpired: Boolean = false
}

trait NoExpiration {
  def isExpired: Boolean = false
}

//object Instrument{
//
//  trait Results[T] extends PricingEngine.Results{
//    def value: Double
//    def errorEstimate: Double
//    def valuationDate: LocalDate
//    def additionalResults: Map[String, T]
//  }
//
//}