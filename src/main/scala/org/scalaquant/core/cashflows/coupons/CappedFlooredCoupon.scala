package org.scalaquant.core.cashflows.coupons

import org.scalaquant.core.types.Rate

case class CappedFlooredCoupon(underlying: FloatingRateCoupon, floor: Option[Rate], cap: Option[Rate]) {

  (floor,cap) match {
    case (Some(floorRate), Some(capRate)) =>
      require(capRate >= floorRate, s"cap level (${cap.get}) less than floor level (${floor.get})"))
    case _ =>
  }

  private def effective(rate: Rate) = (rate - underlying.spread) / underlying.gearing

  def effectiveCap: Option[Rate] = cap.map(effective)
  def effectiveFloor: Option[Rate] = floor.map(effective)
}

object CappedFlooredCoupon{

 // def rate: Rate
}