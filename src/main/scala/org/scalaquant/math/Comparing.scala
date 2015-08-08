package org.scalaquant.math

import org.joda.time.{LocalDate, LocalTime}
import org.scalaquant.math.Comparison._

object Comparing {

  object ImplicitsOps{
    
    implicit class EqualityOpsClass[T: Equality](val self: T) extends EqualityOps[T] {
      def ===(other: T): Boolean = implicitly[Equality[T]].==(self, other)
    }

    implicit class InEqualityOpsClass[T: InEquality](val self: T) extends InEqualityOps[T] {
      def =/=(other: T): Boolean = implicitly[Equality[T]].!=(self, other)
    }

    implicit class OrderOpsClass[T : Equality with InEquality with Order](val self: T) extends OrderOps[T] {
      private val F = implicitly[Equality[T] with InEquality[T] with Order[T]]
      def >(other: T): Boolean = F.>(self, other)
      def <(other: T): Boolean = F.<(self, other)
      def >=(other: T): Boolean =  >(other) || F.==(other)
      def <=(other: T): Boolean =  <(other) || F.==(other)
    }

    implicit class ProximityOpsClass[T: Proximity](val self: T) extends ProximityOps[T]{
      def ~=(other: T, size: Int): Boolean = implicitly[Proximity[T]].~=(self, other, size)
    }
  }

  object Implicits {

    implicit object LocalDateRelational extends Equality[LocalDate] with InEquality[LocalDate] with Order[LocalDate]{
      def ==(x: LocalDate, y: LocalDate): Boolean = x.isEqual(y)
      def <(x: LocalDate, y: LocalDate): Boolean = x.isBefore(y)
      def >(x: LocalDate, y: LocalDate): Boolean = x.isAfter(y)
      def !=(x: LocalDate, y: LocalDate): Boolean = !x.isEqual(y)
    }

    implicit object LocalTimeRelational extends  Equality[LocalTime] with InEquality[LocalTime] with Order[LocalTime]{
      def ==(x: LocalTime, y: LocalTime): Boolean = x.isEqual(y)
      def <(x: LocalTime, y: LocalTime): Boolean = x.isBefore(y)
      def >(x: LocalTime, y: LocalTime): Boolean = x.isAfter(y)
      def !=(x: LocalTime, y: LocalTime): Boolean = ! ==(x, y)
    }

    type D = {def date: LocalDate}

    implicit object RelationalWithDate extends Equality[D] with  InEquality[D]  with Order[D] {
      import Implicits.{ LocalDateRelational => F}
      def ==(x: D, y: D): Boolean = F.==(x.date, y.date)
      def !=(x: D, y: D): Boolean = F.!=(x.date, y.date)
      def <(x: D,y: D): Boolean = F.<(x.date, y.date)
      def >(x: D,y: D): Boolean = F.>(x.date, y.date)
    }

    implicit object DoubleProximity extends Proximity[Double] {
      def ~=(x:Double ,y: Double, size: Int): Boolean = {
        if (x == y) {
          true
        } else {
          val diff = Math.abs( x - y )
          val tolerance = size * Constants.QL_EPSILON
          if (x * y == 0.0) // x or y = 0.0
            diff < (tolerance * tolerance)
          else
            diff <= tolerance * Math.abs(x) || diff <= tolerance * Math.abs(y)
        }
      }
    }
  }

}