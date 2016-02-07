package org.scalaquant.core.cashflows.coupons.pricers

import org.scalaquant.core.types._

trait Pricer {

  def swapletPrice: Rate

  def swapletRate: Rate

  def capletPrice(effectiveCap: Rate): Rate

  def capletRate(effectiveCap: Rate): Rate

  def floorletPrice(effectiveFloor: Rate): Rate

  def floorletRate(effectiveFloor: Rate): Rate

}


