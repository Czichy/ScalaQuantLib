package org.scalaquant.core.indexes.ibor

import org.scalaquant.core.common.time.BusinessDayConvention.ModifiedFollowing
import org.scalaquant.core.common.time.daycounts.{Actual365Fixed, DayCountConvention}
import org.scalaquant.core.common.time.{BusinessDayConvention, Period}
import org.scalaquant.core.currencies.{Asia, Currency}
import org.scalaquant.core.termstructures.YieldTermStructure

/**
  * Created by Neo Lin on 2016-01-30.
  */
final case class Tibor(familyName: String = "Tibor",
                       tenor: Period,
                       fixingDays: Int = 2,
                       currency: Currency = Asia.JPY,
                       //fixingCalendar: BusinessCalendar = Japan(),
                       convention: BusinessDayConvention = ModifiedFollowing,
                       dayCountConvention: DayCountConvention = Actual365Fixed(),
                       forwardingTermStructure:YieldTermStructure)
  extends Libor

