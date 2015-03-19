package org.scalaquant.core.common.time

/** Frequency of events */

class Frequency(val value: Int) extends AnyVal

object Frequency {

  val NoFrequency = new Frequency(-1)
  val Once = new Frequency(0)
  val Annual = new Frequency(1)
  val Semiannual = new Frequency(2)
  val EveryFourthMonth = new Frequency(3)
  val Quarterly = new Frequency(4)
  val Bimonthly = new Frequency(6)
  val Monthly = new Frequency(12)
  val EveryFourthWeek = new Frequency(13)
  val Biweekly = new Frequency(26)
  val Weekly = new Frequency(52)
  val Daily = new Frequency(365)
  val OtherFrequency = new Frequency(999)

  implicit def freqtoString(freq: Frequency): String = freq match {
      case NoFrequency => "No-Frequency"
      case Once => "Once"
      case Annual => "Annual"
      case Semiannual => "Semiannual"
      case EveryFourthMonth => "Every-Fourth-Month"
      case Quarterly => "Quarterly"
      case Bimonthly => "Bimonthly"
      case Monthly => "Monthly"
      case EveryFourthWeek => "Every-fourth-week"
      case Biweekly => "Biweekly"
      case Weekly => "Weekly"
      case Daily => "Daily"
      case OtherFrequency => "Unknown frequency"
      case _ => "Undefined frequency ("+ freq.value +")"
  }

}
