package org.scalaquant.core.instruments.fixedincomes

sealed trait InterestPaymentType

object InterestPaymentType{
  case object Fixed
  case object Floating
}