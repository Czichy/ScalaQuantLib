package org.scalaquant.core.common

object DefaultProtection {
  sealed trait Side
  case object Buyer extends Side
  case object Seller extends Side
}
