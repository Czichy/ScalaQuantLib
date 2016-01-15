package org.scalaquant.core.instruments.fixedincomes

object InterestPaymentType{
  sealed trait InterestPaymentType
  case object Fixed
  case object Floating
}