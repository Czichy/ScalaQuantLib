package org.scalaquant.core.cashflows.coupons

import org.scalaquant.core.cashflows.DigitalReplication
import org.scalaquant.core.common.Position
import org.scalaquant.core.types.Rate


/**
  * Created by Neo Lin on 2016-01-16.
  */
final case class DigitalOption(strike:  Option[Rate] = None,
                         position: Position.Type= Position.Long,
                         isTMIncluded: Boolean = false,
                         payoff: Option[Rate] = None)

final case class DigitalCoupon[+C <: FloatingRateCoupon](underlying: C,
                                                   callSide: DigitalOption,
                                                   putSide: DigitalOption,
                                                   replication: DigitalReplication)
