package org.scalaquant.core.common.time

object TimeUnit{
  sealed trait TimeUnit
  case object Days extends TimeUnit
  case object Weeks extends TimeUnit
  case object Months extends TimeUnit
  case object Years extends TimeUnit
}