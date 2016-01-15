package org.scalaquant.math


object Calculation {

  trait NumberLike[T]

  type Operator[A] = (A, A) => A

  //From http://milessabin.com/blog/2011/06/09/scala-union-types-curry-howard/
//  type ¬[A] = A => Nothing
//  type ∨[T, U] = ¬[¬[T] with ¬[U]]
//  type ¬¬[A] = ¬[¬[A]]
//  type |∨|[T, U] = { type λ[X] = ¬¬[X] <:< (T ∨ U) }
//
//  type U = (Double |∨| Int)#λ //Union Type of Int and Double
//  type I = Double with Int //Intersect Type of Int and Double

  trait Arithmetic[A <: NumberLike[A]]{
    def +(x: A, y: A): A
    def -(x: A, y: A): A
    def *(x: A, y: A): A
    def /(x: A, y: A): A
  }

//  trait ArithmeticWithAnyVal[A <: NumberLike[A]]{
//    def +(x: I, y: A): A
//    def -(x: I, y: A): A
//    def *(x: I, y: A): A
//    def /(x: I, y: A): A
//    def +(x: A, y: I): A
//    def -(x: A, y: I): A
//    def *(x: A, y: I): A
//    def /(x: A, y: I): A
//  }

  trait ArithmeticOps[T <: NumberLike[T]] {
    def +(y: T): T
    def -(y: T): T
    def *(y: T): T
    def /(y: T): T
//    def +(y: I): T
//    def -(y: I): T
//    def *(y: I): T
//    def /(y: I): T
//    def :+(y: I): T
//    def :-(y: I): T
//    def :*(y: I): T
//    def :/(y: I): T

  }

}

object Calculating{
  import Calculation._

  implicit class ArithmeticOpsClass[T](val self: T)(implicit val F: Arithmetic[T] ) extends ArithmeticOps[T]{
     def +(y: T): T = F.+(self, y)
     def /(y: T): T = F./(self, y)
     def -(y: T): T = F.-(self, y)
     def *(y: T): T = F.*(self, y)

  }
}