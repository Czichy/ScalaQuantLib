package org.scalaquant.core.common.time

/**
 * Created by neo on 2015-03-01.
 */
object DateGeneration extends Enumeration {
  type Rule = Value
  val Backward, Forward, Zero, ThirdWednesday, Twentieth, TwentiethIMM, OldCDS, CDS = Value
}
