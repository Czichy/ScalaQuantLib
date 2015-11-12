package org.scalaquant.core.cashflows.coupons


import org.joda.time.LocalDate
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.cashflows.CPI.InterpolationType
import org.scalaquant.core.indexes.inflation.ZeroInflationIndex
import org.scalaquant.core.types._

class CPICoupon(val baseCPI: Double,
 paymentDate: LocalDate,
 nominal: LocalDate,
 startDate: LocalDate,
 endDate: LocalDate,
 fixingDays: Int,
 index: ZeroInflationIndex,
 observationLag: Period,
 observationInterpolation: InterpolationType,
                dayCounter: DayCountConvention,
 fixedRate: Rate, // aka gearing
 spread: Spread = 0.0,
refPeriodStart: LocalDate,
refPeriodEnd: LocalDate,

exCouponDate: LocalDate) extends InflationCoupon(
  paymentDate,
  nominal,
  startDate,
  endDate,
  fixingDays,
index,
 observationLag,
 dayCounter: DayCountConvention,
refPeriodStart: LocalDate,
refPeriodEnd: LocalDate,
exCouponDate: LocalDate
)
)

//! \name Inspectors
//@{
//! fixed rate that will be inflated by the index ratio
Real fixedRate() const;
//! spread paid over the fixing of the underlying index
Spread spread() const;

//! adjusted fixing (already divided by the base fixing)
Rate adjustedFixing() const;
//! allows for a different interpolation from the index
Rate indexFixing() const;
//! base value for the CPI index
/*! \warning make sure that the interpolation used to create
             this is what you are using for the fixing,
             i.e. the observationInterpolation.
*/
Rate baseCPI() const;
//! how do you observe the index?  as-is, flat, linear?
CPI::InterpolationType observationInterpolation() const;
//! utility method, calls indexFixing
Rate indexObservation(const Date& onDate) const;
//! index used
boost::shared_ptr<ZeroInflationIndex> cpiIndex() const;) {

}
