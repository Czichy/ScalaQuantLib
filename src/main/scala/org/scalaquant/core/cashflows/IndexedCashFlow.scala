package org.scalaquant.core.cashflows

import java.time.LocalDate
import org.scalaquant.core.cashflows.coupons.CPI
import org.scalaquant.core.cashflows.coupons.CPI.InterpolationType

import org.scalaquant.core.common.time.{TimeUnit, Period, Frequency}
import org.scalaquant.core.common.time.Frequency.Frequency
import org.scalaquant.core.common.time.JodaDateTimeHelper._

import org.scalaquant.core.indexes.Index
import org.scalaquant.core.indexes.inflation.{InflationIndex, ZeroInflationIndex}
import org.scalaquant.core.types.Rate



class IndexedCashFlow(val notional: Double,
                      val index: Index,
                      val baseDate: LocalDate,
                      val fixingDate: LocalDate,
                      paymentDate: LocalDate,
                      val growthOnly: Boolean = false) extends CashFlow{

  def date = paymentDate

  def amount: Double = {
    val I0 = index.fixing(baseDate)
    val I1 = index.fixing(fixingDate)

    if (growthOnly) notional * (I1 / I0 - 1.0) else notional * (I1 / I0)
  }

}

class CPICashFlow(notional: Double,
                  index: ZeroInflationIndex,
                  baseDate: LocalDate,
                  fixingDate: LocalDate,
                  paymentDate: LocalDate,
                  growthOnly: Boolean,
                  val baseFixing: Rate,
                  val interpolation: InterpolationType = CPI.AsIndex,
                  val frequency: Frequency = Frequency.NoFrequency)
  extends IndexedCashFlow(notional,
                          index,
                          baseDate,
                          fixingDate,
                          paymentDate,
                          growthOnly){
  require(Math.abs(baseFixing) > 1e-16, "|baseFixing| < 1e-16, future divide-by-zero problem")

  if (interpolation != CPI.AsIndex) require(frequency != Frequency.NoFrequency, "non-index interpolation w/o frequency")

  override def amount = {

      val I0 = baseFixing
      val I1 = CPI.indexFixing(fixingDate, interpolation, index, frequency)

      if (growthOnly) notional * (I1 / I0 - 1.0) else notional * (I1 / I0)
  }

}