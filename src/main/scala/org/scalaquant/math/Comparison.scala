package org.scalaquant.math


object Comparison {

  trait Equality[A]{
    def ==(x: A, y: A): Boolean
  }

  trait EqualityOps[A]{
    def ===(other: A): Boolean
  }

  trait InEquality[A]{
    def !=(x: A, y: A): Boolean
    def >(x: A, y: A): Boolean
    def <(x: A, y: A): Boolean
  }

  trait InEqualityOps[A]{
    def =/=(other: A): Boolean
    def >(other: A): Boolean
    def <(other: A): Boolean
  }

  trait Relational[A] extends Equality[A] with InEquality[A] {
    def >=(x: A, y: A): Boolean = >(x,y) || ==(x,y)
    def <=(x: A, y: A): Boolean = <(x,y) || ==(x,y)
  }

  trait RelationalOps[A] extends EqualityOps[A] with InEqualityOps[A] {
    def >=(other: A): Boolean = >(other) || ===(other)
    def <=(other: A): Boolean = <(other) || ===(other)
  }

  trait Proximity[A]{
    def ~=(x: A, y: A): Boolean = ~=(x,y, 42)
    def ~=(x: A, y: A, size: Int): Boolean
  }

  trait ProximityOps[A]{
    def ~=(other: A): Boolean = ~=(other, 42)
    def ~=(other: A, size: Int): Boolean
  }


}
