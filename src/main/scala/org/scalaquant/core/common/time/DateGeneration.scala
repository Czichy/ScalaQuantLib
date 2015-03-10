package org.scalaquant.core.common.time

/**
 * Created by neo on 2015-03-01.
 */
object DateGeneration {

  sealed trait Rule

  case object Backward extends Rule
  case object Forward extends Rule
  case object Zero extends Rule
  case object ThirdWednesday extends Rule
  case object Twentieth extends Rule
  case object TwentiethIMM extends Rule
  case object OldCDS extends Rule
  case object CDS extends Rule
}
