package org.scalaquant.core.cashflows.coupons


import org.joda.time.LocalDate
import org.scalaquant.core.common.time.Period
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import CPI.InterpolationType
import org.scalaquant.core.indexes.inflation.ZeroInflationIndex
import org.scalaquant.core.types._

object CPI {

  sealed trait InterpolationType

  case object AsIndex extends InterpolationType   //!< same interpolation as index
  case object Flat extends InterpolationType     //!< flat from previous fixing
  case object Linear extends InterpolationType    //!< linearly between bracketing fixings
}

class CPICoupon(val baseCPI: Double,
                 paymentDate: LocalDate,
                 nominal: Rate,
                 accrualStartDate: LocalDate, //usually the payment date of last coupon
                 accrualEndDate: LocalDate, //usually the sttlement date of the coupon
                 refPeriodStart: Option[LocalDate],
                 refPeriodEnd: Option[LocalDate],
                 fixingDays: Natural,
                 index: ZeroInflationIndex,
                 observationLag: Period,
                 val observationInterpolation: InterpolationType,
                 dayCounter: DayCountConvention,
                 val fixedRate: Rate, // aka gearing
                 val spread: Spread = 0.0,
                 exCouponDate: Option[LocalDate])
  extends InflationCoupon(paymentDate,
                          nominal,
                          accrualStartDate,
                          accrualEndDate,
                          refPeriodStart,
                          refPeriodEnd,
                          fixingDays,
                          index,
                          observationLag,
                          dayCounter,
                          exCouponDate){

  require(Math.abs(baseCPI) > 1e-16, "|baseCPI| < 1e-16, future divide-by-zero problem")

//! adjusted fixing (already divided by the base fixing)
def adjustedFixing: Rate
//! allows for a different interpolation from the index
//private def indexFixing: Rate
//  def indexObservation(onDate:LocalDate): Double
//! index used
def cpiIndex: ZeroInflationIndex

}
