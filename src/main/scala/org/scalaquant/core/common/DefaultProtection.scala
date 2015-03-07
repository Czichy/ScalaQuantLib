package org.scalaquant.core.common

/**
 * Created by neo on 2015-03-01.
 */

object DefaultProtection {
  sealed trait Side
  case object Buyer extends Side
  case object Seller extends Side
}
