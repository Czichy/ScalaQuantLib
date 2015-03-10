package org.scalaquant.core.common

import org.joda.time.ReadablePartial

case class TimeSeries[T <: ReadablePartial, V](timeSeq: Seq[T], valueSeq: Seq[V]) {
  private val pairs = (timeSeq zip valueSeq).toMap

  def firstDate: Option[T] = if (pairs.isEmpty) None else pairs.keys.headOption
  def lastDate: Option[T] = if (pairs.isEmpty) None else pairs.keys.lastOption

  def size: Int = pairs.size
  def isEmpty: Boolean = pairs.isEmpty

  def find(key: T): Option[V] = pairs.get(key)

  def dates: Seq[T] = pairs.keys.toSeq
  def values: Seq[V] = pairs.values.toSeq

  def reverse: Map[T, V] =  pairs.keys.toList.reverse.map(key => (key, pairs(key))).toMap

}