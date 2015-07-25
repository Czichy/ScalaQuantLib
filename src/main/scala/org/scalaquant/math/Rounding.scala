package org.scalaquant.math

sealed trait Rounding {
  import Rounding._

  def precision: Int
  def digit: Int
  def roundingType: Type

  def apply(oldValue: Double): Double = {
    val multiplier = Math.pow(10.0, precision)
    val isNegative = oldValue < 0.0
    val largeValue = Math.abs(oldValue) * multiplier
    val integral = largeValue.toLong
    val fractional = largeValue - integral

    def roundUp = (integral + 1) / multiplier
    def roundDown = integral / multiplier
    def roundToClosest = if (fractional >= (digit / 10.0)) roundUp else roundDown

    val newValue = this.roundingType match {
      case Up => roundUp
      case Down => roundDown
      case Closest => roundToClosest
      case Floor => if (!isNegative) roundToClosest else roundDown
      case Ceiling => if (isNegative) roundToClosest else roundDown
      case None => Math.abs(oldValue)
    }
    if (isNegative) -newValue else newValue
  }

}

object Rounding {

  sealed trait Type
  case object None extends Type
  case object Up extends Type
  case object Down extends Type
  case object Closest extends Type
  case object Floor extends Type
  case object Ceiling extends Type
}

case class NoRounding(precision: Int = 0, digit: Int = 5) extends Rounding {
  val roundingType: Rounding.Type = Rounding.None
}

case class DownRounding(precision: Int, digit: Int = 5) extends Rounding {
  val roundingType: Rounding.Type = Rounding.Down
}

case class UpRounding(precision: Int, digit: Int = 5) extends Rounding {
  val roundingType: Rounding.Type = Rounding.Up
}

case class ClosestRounding(precision: Int, digit: Int = 5) extends Rounding {
  val roundingType: Rounding.Type = Rounding.Closest
}

case class CeilingRounding(precision: Int, digit: Int = 5) extends Rounding {
  val roundingType: Rounding.Type = Rounding.Ceiling
}

case class FloorRounding(precision: Int, digit: Int = 5) extends Rounding {
  val roundingType: Rounding.Type = Rounding.Floor
}
