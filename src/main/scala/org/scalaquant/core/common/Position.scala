package org.scalaquant.core.common

/**
 * Created by neo on 2015-03-01.
 */

object Position {
  sealed trait Type
  case object Long extends Type
  case object Short extends Type
}
