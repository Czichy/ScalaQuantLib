package org.scalaquant.core.common

import org.joda.time.LocalDate

abstract class Exercise(exerciseType: Exercise.Type) {
  protected val dates: List[LocalDate]
  def date(n: Int): Option[LocalDate] = dates.lift(n)
  def lastDate: Option[LocalDate] = dates.lastOption
}

abstract class EarlyExercise(val exerciseType: Exercise.Type,
                             val payoffAtExpiry: Boolean = false) extends Exercise(exerciseType)

final case class AmericanExercise(earliest: LocalDate = farPass,
                                  latest: LocalDate,
                                  override val payoffAtExpiry: Boolean = false)
  extends EarlyExercise(Exercise.American, payoffAtExpiry){

  override val dates = {
    require(earliest <= latest, "earliest > latest exercise date")
    List(earliest, latest)
  }
}

final case class BermudanExercise(override val dates: List[LocalDate],
                                  override val payoffAtExpiry: Boolean = false)
  extends EarlyExercise(Exercise.Bermudan, payoffAtExpiry)

case class EuropeanExercise(date: LocalDate) extends Exercise(Exercise.European) {
  override val dates = List(date)
}

object Exercise {
  sealed trait Type
  case object American extends Type
  case object Bermudan extends Type
  case object European extends Type
}