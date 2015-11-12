package org.scalaquant.core.common

import org.scalaquant.math.Comparison._

class TimeGrid(start: Double, end: Double, steps: Int) {
  require(end >= start)

  //warning. limitation on the memory size for this approach
  private def unique(original: List[Double], f: (Double, Double) => Boolean): List[Double] ={
    original match {
      case head :: tail =>
        tail match {
          case head2 :: tail2 => if (f(head, head2)) unique(tail,f) else head :: unique(tail, f)
          case Nil => head :: Nil
        }
      case Nil => Nil
    }
  }
  private def _interval =  (end - start) / steps

  private var _deltas: List[Double] = List.fill(steps)(_interval)

  private var _mandatoryTimes: List[Double] = (0 to steps).map(_ * _interval + start).toList

  private var _times: List[Double] = _mandatoryTimes


  def this(mandatoryTimes: List[Double]){
    this()
    _mandatoryTimes = mandatoryTimes.sorted
    _times = unique(_mandatoryTimes, _ ~= _ )
    _deltas = (_times, _times drop 1).zipped.map(_-_)
  }

  def this(mandatoryTimes: List[Double], steps: Int){
    this
    _mandatoryTimes = mandatoryTimes.sorted
    val interval = if (steps == 0) (_mandatoryTimes, _mandatoryTimes drop 1).zipped.map(_-_).min else (_mandatoryTimes.last - _mandatoryTimes.head) / steps
    _times = (0 to steps).map(_ * interval + _mandatoryTimes.head).toList
    _deltas = List.fill(steps)(interval)
  }

  def deltaAt(i: Int): Double = _deltas(i)

  def closestIndex(time: Double): Int = _times.zipWithIndex.find(_._1 ~= time).map(_._2).getOrElse(0)

  def closestTime(time: Double): Double = _times(closestIndex(time))

  def mandatoryTimes: List[Double] = _mandatoryTimes


  def size = _times.size
  def isEmpty = _times.isEmpty

  val head = _times.head
  val last = _times.last


}
