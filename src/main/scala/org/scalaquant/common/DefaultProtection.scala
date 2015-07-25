package org.scalaquant.common

object DefaultProtection {
  sealed trait Side
  case object Buyer extends Side
  case object Seller extends Side
}
