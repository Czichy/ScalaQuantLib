package org.scalaquant.core.common


object Position {
  sealed trait Type
  case object Long extends Type
  case object Short extends Type
}
