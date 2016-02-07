package org.scalaquant.core.termstructures.volatility

import java.time.LocalDate
import java.util.Calendar

import org.scalaquant.core.common.time.BusinessDayConvention
import org.scalaquant.core.common.time.calendars.BusinessCalendar
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.indexes.ibor.IBORIndex
import org.scalaquant.core.types.{Rate, Natural}

/**
  * Created by neo on 2/5/16.
  */
final case class StrippedOptionlet(settlementDays: Natural,
                                   calendar: BusinessCalendar ,
                                   bdc: BusinessDayConvention,
                                   iborIndex: IBORIndex,
                                   optionletDates: List[LocalDate],
                                   strikes: List[Rate],
//const std::vector<std::vector<Handle<Quote> > >&,
                                   dayConvention: DayCountConvention)


trait StrippedOptionletBase {

  def optionletStrikes
  def optionletVolatilities

  def optionletFixingDates
  def optionletFixingTimes
  def optionletMaturities

  def atmOptionletRates
}

