package org.scalaquant.common.time

/** Frequency of events */

object Frequency {

   class Frequency(val value: Int) extends AnyVal {

     override def toString = value match {
      case NoFrequency.value => "No-Frequency"
      case Once.value => "Once"
      case Annual.value => "Annual"
      case Semiannual.value => "Semiannual"
      case EveryFourthMonth.value => "Every-Fourth-Month"
      case Quarterly.value => "Quarterly"
      case Bimonthly.value => "Bimonthly"
      case Monthly.value => "Monthly"
      case EveryFourthWeek.value => "Every-fourth-week"
      case Biweekly.value => "Biweekly"
      case Weekly.value => "Weekly"
      case Daily.value => "Daily"
      case OtherFrequency.value => "Unknown frequency"
      case e => "Invalid value for ("+ e +") event frequency"
    }
  }

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

}
