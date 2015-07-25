package org.scalaquant.core.cashflows.coupons

import org.joda.time.LocalDate
import org.scalaquant.common.time.JodaDateTimeHelper._
import org.scalaquant.core.cashflows.CashFlow
import org.scalaquant.core.types.Rate

import scala.language.implicitConversions

abstract class Coupon(val paymentDate: LocalDate, //the upcoming payment date of this coupon
                      val nominal: Rate,
                      val accrualStartDate: LocalDate, //usually the payment date of last coupon
                      val accrualEndDate: LocalDate, //usually the settlement date of the coupon
                      val exCouponDate: LocalDate) extends CashFlow {

  override def date: LocalDate = paymentDate

  private def isNotInAccrualPeriod(date: LocalDate) = date <= accrualStartDate || date > paymentDate

//  def accrualDays: Int = dayCounter.dayCount(accrualStartDate, accrualEndDate)
//
//  def accrualPeriod: YearFraction = dayCounter.fractionOfYear(accrualStartDate, accrualEndDate)

//  def accruedDays(date: LocalDate): Int = {
//    if (isNotInAccrualPeriod(date)) 0
//    else dayCounter.dayCount(accrualStartDate, min(date, accrualEndDate))
//  }
//  def accruedPeriod(date: LocalDate): YearFraction = {
//    if (isNotInAccrualPeriod(date))
//      0.0
//    else
//      dayCounter.fractionOfYear(accrualStartDate, min(date, accrualEndDate), paymentDate)
//  }

 // def accruedAmount(date: LocalDate): Double

 // def rate: Rate

  //def dayCounter: DayCountConvention

  //override def amount: Double = rate * accrualPeriod * nominal

}


//trait AmountCalculation { self: Coupon =>
//  private def startDate: LocalDate
//  private def endDate: LocalDate
//
//  override def accruedAmount(date: LocalDate): Rate = {
//    if (date <= accrualStartDate || date > paymentDate)
//      0.0
//    else
//      nominal * rate * dayCounter.fractionOfYear(startDate, min(date, endDate))
//  }
//
//  def price(discountingCurve: YieldTermStructure): Double = amount * discountingCurve.discount(date)
//
//}
