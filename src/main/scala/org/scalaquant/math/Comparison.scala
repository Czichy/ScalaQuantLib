package org.scalaquant.math


object Comparison {
  type Operator = (Double, Double) => Boolean

  trait Equality[A]{
    def ==(x: A, y: A): Boolean
    def !=(x: A, y: A): Boolean = ! ==(x, y)
  }


  trait Order[A]{
    def >(x: A, y: A): Boolean
    def <(x: A, y: A): Boolean
    def >=(x: A, y: A): Boolean = ! <(x, y)
    def <=(x: A, y: A): Boolean = ! >(x, y)
  }

  trait EqualityOps[A]{
    def ==(other: A): Boolean
    def /=(other: A): Boolean = ! ==(other)
  }


  trait OrderOps[A] {
    def >(other: A): Boolean
    def <(other: A): Boolean
    def >=(other: A): Boolean = ! <(other)
    def <=(other: A): Boolean = ! >(other)
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
