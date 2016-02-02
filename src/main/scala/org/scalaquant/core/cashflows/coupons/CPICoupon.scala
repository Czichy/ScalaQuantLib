package org.scalaquant.core.cashflows.coupons


import java.time.LocalDate
import org.scalaquant.core.common.time.Frequency.Frequency
import org.scalaquant.core.common.time.{TimeUnit, Period}
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.indexes.inflation.{InflationIndex, ZeroInflationIndex}
import org.scalaquant.core.types._

import org.scalaquant.core.common.time.JodaDateTimeHelper._

object CPI {

  sealed trait InterpolationType

  case object AsIndex extends InterpolationType   //!< same interpolation as index
  case object Flat extends InterpolationType     //!< flat from previous fixing
  case object Linear extends InterpolationType    //!< linearly between bracketing fixings

  final def indexFixing(fixingDate: LocalDate,
                  interpolation: InterpolationType,
                  index: InflationIndex,
                  frequency: Frequency): Rate = {

    if (interpolation == CPI.AsIndex) {
      index.fixing(fixingDate)
    } else {
      // work out what it should be
      val (startDate, endDate) = InflationIndex.inflationPeriod(fixingDate, frequency)
      val indexStart = index.fixing(startDate)

      if (interpolation == CPI.Linear) {
        val indexEnd = index.fixing(endDate + Period(1, TimeUnit.Days))
        // linear interpolation
        indexStart + (indexEnd - indexStart) * (fixingDate - startDate) / (endDate + Period(1, TimeUnit.Days) - startDate)
        // can't get to next period's value within current period
      } else {
        indexStart
      }
    }
  }

}



case class CPICoupon(baseCPI: Double,
                     paymentDate: LocalDate,
                     nominal: Rate,
                     accrualStartDate: LocalDate, //usually the payment date of last coupon
                     accrualEndDate: LocalDate, //usually the sttlement date of the coupon
                     refPeriodStart: Option[LocalDate],
                     refPeriodEnd: Option[LocalDate],
                     exCouponDate: Option[LocalDate],
                     fixingDays: Natural,
                     index: ZeroInflationIndex,
                     observationLag: Period,
                     observationInterpolation: CPI.InterpolationType,
                     dayCounter: DayCountConvention,
                     fixedRate: Rate, // aka gearing
                     spread: Spread = 0.0)
  extends InflationCoupon {

  require(Math.abs(baseCPI) > 1e-16, "|baseCPI| < 1e-16, future divide-by-zero problem")

  def indexFixing(fixingDate: LocalDate): Rate = {
    CPI.indexFixing(fixingDate, observationInterpolation, index, index.frequency)
  }

  //def adjustedFixing = rate - spread / fixedRate

}