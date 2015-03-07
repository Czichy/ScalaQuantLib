package org.scalaquant.core.common.time

/**
 * Created by neo on 2015-03-01.
 */
object Frequency extends Enumeration {
  type Frequency = Value
  val NoFrequency = Value(-1)
  val Once = Value(0)
  val Annual = Value(1)
  val Semiannual = Value(2)
  val EveryFourthMonth = Value(3)
  val Quarterly = Value(4)
  val Bimonthly = Value(6)
  val Monthly = Value(12)
  val EveryFourthWeek = Value(13)
  val Biweekly = Value(26)
  val Weekly = Value(52)
  val Daily = Value(365)
  val OtherFrequency = Value(999)
}
