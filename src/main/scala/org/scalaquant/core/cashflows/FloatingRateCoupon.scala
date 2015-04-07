package org.scalaquant.core.cashflows

import org.joda.time.LocalDate

import org.scalaquant.core.common.time.Frequency
import org.scalaquant.core.common.time.daycounts.DayCountConvention
import org.scalaquant.core.indexes.InterestRateIndex
import org.scalaquant.core.termstructures.YieldTermStructure
import rx.lang.scala.Observer


abstract class FloatingRateCoupon( override val paymentDate: LocalDate,
                          override val nominal: Double,
                          val startDate: LocalDate,
                          val endDate: LocalDate,
                          val fixingDays: Int,
                          val index: InterestRateIndex,
                          val gearing: Double = 1.0,
                          val spread: Double = 0.0,
                          val freq: Frequency,
                          val dayCounter: DayCountConvention,
                          val isInArrears: Boolean = false) extends Coupon(paymentDate, nominal, startDate, endDate)
                                                            with Observer[Double]{
      require(gearing != 0.0, "empty gearing not allowed")

  override def accruedAmount(date: LocalDate): Double = ???

  override def amount: Double = rate * accrualPeriod * nominal

  def price(discountingCurve: YieldTermStructure): Double = ???

  def indexFixing: Double

  def convexityAdjustment: Double
}
