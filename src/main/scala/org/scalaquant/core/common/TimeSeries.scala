package org.scalaquant.core.common

import java.time.ReadablePartial

case class TimeSeries[T <: ReadablePartial, V <: AnyVal](pairs: Map[T, V]) {
  def firstDate: T = pairs.keys.head
  def lastDate: T = pairs.keys.last

  def size: Int = pairs.size
  def isEmpty: Boolean = pairs.isEmpty
  def nonEmpty: Boolean = pairs.nonEmpty

  def find(key: T): Option[V] = pairs.get(key)

  def get(key: T): V = pairs.apply(key)

  def dates: Seq[T] = pairs.keys.toSeq
  def values: Seq[V] = pairs.values.toSeq

  def reverse: Map[T, V] =  pairs.keys.toList.reverse.map(key => (key, pairs(key))).toMap

}