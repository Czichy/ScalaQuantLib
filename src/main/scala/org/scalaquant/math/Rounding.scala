package org.scalaquant.math

sealed trait Rounding {
  def precision: Int
  def digit: Int
  def roundingType: RoundingType

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
      case RoundingType.Up => roundUp
      case RoundingType.Down => roundDown
      case RoundingType.Closest => roundToClosest
      case RoundingType.Floor => if (!isNegative) roundToClosest else roundDown
      case RoundingType.Ceiling => if (isNegative) roundToClosest else roundDown
      case RoundingType.None => Math.abs(oldValue)
    }
    if (isNegative) -newValue else newValue
  }
}

sealed trait RoundingType
object RoundingType {
  case object None extends RoundingType
  case object Up extends RoundingType
  case object Down extends RoundingType
  case object Closest extends RoundingType
  case object Floor extends RoundingType
  case object Ceiling extends RoundingType
}

case class NoRounding(precision: Int = 0, digit: Int = 5) extends Rounding {
  val roundingType: RoundingType = RoundingType.None
}
case class DownRounding(precision: Int, digit: Int = 5) extends Rounding {
  val roundingType: RoundingType = RoundingType.Down
}
case class UpRounding(precision: Int, digit: Int = 5) extends Rounding {
  val roundingType: RoundingType = RoundingType.Up
}
case class ClosestRounding(precision: Int, digit: Int = 5) extends Rounding {
  val roundingType: RoundingType = RoundingType.Closest
}
case class CeilingRounding(precision: Int, digit: Int = 5) extends Rounding {
  val roundingType: RoundingType = RoundingType.Ceiling
}
case class FloorRounding(precision: Int, digit: Int = 5) extends Rounding {
  val roundingType: RoundingType = RoundingType.Floor
}
