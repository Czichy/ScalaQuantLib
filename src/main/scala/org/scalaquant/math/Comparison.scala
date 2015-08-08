package org.scalaquant.math


object Comparison {
  trait Equality[A]{
    def ==(x: A, y: A): Boolean
  }

  trait InEquality[A]{
    def !=(x: A, y: A): Boolean
  }

  trait Order[A]{
    def >(x: A, y: A): Boolean
    def <(x: A, y: A): Boolean
  }

  trait EqualityOps[A]{
    def ===(other: A): Boolean
  }

  trait InEqualityOps[A]{
    def =/=(other: A): Boolean
  }

  trait OrderOps[A] {
    def >(other: A): Boolean
    def <(other: A): Boolean
    def >=(other: A): Boolean
    def <=(other: A): Boolean
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
