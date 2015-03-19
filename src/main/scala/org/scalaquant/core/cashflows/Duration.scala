package org.scalaquant.core.cashflows

object Duration {
  sealed trait Type
  case object Simple extends Type
  case object Macaulay extends Type
  case object Modified extends Type
}
