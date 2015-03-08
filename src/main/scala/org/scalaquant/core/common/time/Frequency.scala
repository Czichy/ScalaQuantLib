package org.scalaquant.core.common.time

/**
 * Created by neo on 2015-03-01.
 */

case class Frequency(value:Int) extends AnyVal

object Frequency {
  val NoFrequency = Frequency(-1)
  val Once = Frequency(0)
  val Annual = Frequency(1)
  val Semiannual = Frequency(2)
  val EveryFourthMonth = Frequency(3)
  val Quarterly = Frequency(4)
  val Bimonthly = Frequency(6)
  val Monthly = Frequency(12)
  val EveryFourthWeek = Frequency(13)
  val Biweekly = Frequency(26)
  val Weekly = Frequency(52)
  val Daily = Frequency(365)
  val OtherFrequency = Frequency(999)
}
