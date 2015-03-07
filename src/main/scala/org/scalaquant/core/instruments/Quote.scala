package org.scalaquant.core.instruments

import rx.lang.scala.Observable

/**
 * Created by neo on 2015-02-28.
 */
trait Quote[T] extends Observable[T] {
  def value: Double
  def isValid: Boolean
}
