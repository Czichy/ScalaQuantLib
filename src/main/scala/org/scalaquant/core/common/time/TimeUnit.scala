package org.scalaquant.core.common.time

/**
 * Created by neo on 2015-03-07.
 */
sealed trait TimeUnit

object TimeUnit{
  case object Days extends TimeUnit
  case object Weeks extends TimeUnit
  case object Months extends TimeUnit
  case object Years extends TimeUnit
}