package org.scalaquant.core.indexes.ibor

import org.scalaquant.core.common.time.BusinessDayConvention._
import org.scalaquant.core.common.time.TimeUnit._
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.{Actual360, DayCountConvention}
import org.scalaquant.core.common.time.{BusinessDayConvention, Period}
import org.scalaquant.core.currencies.{Currency, Asia}
import org.scalaquant.core.termstructures.YieldTermStructure

/**
  * Created by Neo Lin on 2016-01-28.
  */
final case class Shibor(familyName: String = "Shibor",
                        tenor: Period = Period(1),
                        convention: BusinessDayConvention = Shibor.convention(this.tenor),
                        fixingDays: Int = 1,
                        currency: Currency = Asia.CNY,
                  //fixingCalendar: BusinessCalendar = China(IB),
                        dayCountConvention: DayCountConvention = Actual360(),
                        forwardingTermStructure:YieldTermStructure)
  extends Libor

object Shibor{
  def convention(p: Period): BusinessDayConvention = {
     p.units match {
       case Days | Weeks => Following
       case Months | Years => ModifiedFollowing
     }
  }
}
