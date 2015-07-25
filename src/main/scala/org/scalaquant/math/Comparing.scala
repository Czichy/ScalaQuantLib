package org.scalaquant.math

import org.joda.time.{LocalTime, LocalDate}
import org.scalaquant.core.currencies.Currency
import org.scalaquant.math.Comparison._

object Comparing {

  object ImplicitsOps{
    
    implicit class EqualityOpsClass[T](val self: T)(implicit val F: Equality[T]) extends EqualityOps[T] {
      def ===(other: T): Boolean = F.==(self, other)
    }

    implicit class InEqualityOpsClass[T](val self: T)(implicit val F: InEquality[T]) extends InEqualityOps[T] {
      def =/=(other: T): Boolean = F.!=(self, other)
      def >(other: T): Boolean = F.>(self, other)
      def <(other: T): Boolean = F.<(self, other)
    }

    implicit class RelationalOpsClass[T](val self: T)(implicit val F: Relational[T]) extends RelationalOps[T] {
      override def =/=(other: T): Boolean = F.!=(self, other)
      override def >(other: T): Boolean = F.>(self, other)
      override def <(other: T): Boolean = F.<(self, other)
      override def ===(other: T): Boolean = F.==(self, other)
    }

    implicit class ProximityOpsClass[T](val self: T)(implicit val F: Proximity[T]) extends ProximityOps[T]{
      def ~=(other: T, size: Int): Boolean = F.~=(self, other, size)
    }
  }

  object Implicits {

    implicit object CurrencyEquality extends Equality[Currency] {
      def ==(x: Currency, y: Currency): Boolean = x.definition.numericCode == y.definition.numericCode
    }

    implicit object LocalDateRelational extends Relational[LocalDate]{
      def ==(x: LocalDate, y: LocalDate): Boolean = x.isEqual(y)
      def <(x: LocalDate, y: LocalDate): Boolean = x.isBefore(y)
      def >(x: LocalDate, y: LocalDate): Boolean = x.isAfter(y)
      def !=(x: LocalDate, y: LocalDate): Boolean = ! ==(x, y)
    }

    implicit object LocalTimeRelational extends Relational[LocalTime]{
      def ==(x: LocalTime, y: LocalTime): Boolean = x.isEqual(y)
      def <(x: LocalTime, y: LocalTime): Boolean = x.isBefore(y)
      def >(x: LocalTime, y: LocalTime): Boolean = x.isAfter(y)
      def !=(x: LocalTime, y: LocalTime): Boolean = ! ==(x, y)
    }

    type D = {def date: LocalDate}
    implicit object RelationalWithDate extends Relational[D] {
      import Implicits.{ LocalDateRelational => F}

      def !=(x: D, y: D): Boolean = F.!=(x.date, y.date)
      def ==(x: D, y: D): Boolean = F.==(x.date, y.date)
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