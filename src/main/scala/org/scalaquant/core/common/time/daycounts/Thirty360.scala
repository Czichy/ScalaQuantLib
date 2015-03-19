package org.scalaquant.core.common.time.daycounts

/**
 * Created by neo on 2015-03-02.
 */

import org.joda.time.LocalDate
import org.scalaquant.core.common.time.Frequency

import scala.language.implicitConversions
import org.scalaquant.core.common.time.JodaDateTimeHelper._

sealed trait Thirty360 extends DayCountConvention {
  type DayMonthPicker = (Int, Int, Int) => Int
  protected val m2Pick: DayMonthPicker
  protected val d1Pick: DayMonthPicker
  protected val d2Pick: DayMonthPicker

  override def dayCount(date1: LocalDate, date2: LocalDate): Int = {
    val (yy1, mm1, dd1) = date1.YMD
    val (yy2, mm2, dd2) = date2.YMD

    val M2 = m2Pick(dd1, dd2, mm2)
    val D1 = d1Pick(dd1, dd2, mm1)
    val D2 = d2Pick(dd1, dd2, mm2)

    360 * (yy2 - yy1) + 30 * (M2 - mm1 - 1) + Math.max(0, 30 - D1) + Math.min(30, D2)
  }
  override def fraction(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency): Double = {
    dayCount(date1, date2) / 360.0
  }
}

object Thirty360 {
  sealed trait Convention

  case object USA extends Convention
  case object BondBasis extends Convention
  case object European extends Convention
  case object EurobondBasis extends Convention
  case object Italian extends Convention

  private val USImplement = new Thirty360 {
    override def name: String = "30/360 (Bond Basis)"
    override val m2Pick: DayMonthPicker = (d1, d2, m) => if (d2 == 31 && d1 < 30) m + 1 else m
    override val d1Pick: DayMonthPicker = (d1, d2, m) => d1
    override val d2Pick: DayMonthPicker = (d1, d2, m) => if (d2 == 31 && d1 < 30) 1 else d2
  }
  private val EUImplement = new Thirty360 {
    override def name: String = "30E/360 (Eurobond Basis)"
    override val m2Pick: DayMonthPicker = (d1, d2, m) => m
    override val d1Pick: DayMonthPicker = (d1, d2, m) => d1
    override val d2Pick: DayMonthPicker = (d1, d2, m) => d2
  }
  private val ITImplement = new Thirty360 {
    override def name: String = "30/360 (Italian)"
    override val m2Pick: DayMonthPicker = (d1, d2, m) => m
    override val d1Pick: DayMonthPicker = (d1, d2, m) => if (m == 2 && d1 > 27) 30 else d1
    override val d2Pick: DayMonthPicker = (d1, d2, m) => if (m == 2 && d1 > 27) 30 else d2
  }

  def apply(convention: Convention = BondBasis): Thirty360 = {
    convention match {
      case USA | BondBasis => USImplement
      case European | EurobondBasis => EUImplement
      case Italian => ITImplement
    }
  }
}

object SimpleDayCountConvention{

  private val simpleImpl = new DayCountConvention  {
    val name: String = "Simple"
    private val fallBack = Thirty360()
    override def dayCount(date1: LocalDate, date2: LocalDate): Int = fallBack.dayCount(date1, date2)
    override def fraction(date1: LocalDate, date2: LocalDate, date3: LocalDate, freq: Frequency): Double = {
      val dm1 = date1.getDayOfMonth
      val dm2 = date2.getDayOfMonth

      if (dm1 == dm2 ||
        // e.g., Aug 30 -> Feb 28 ?
        (dm1 > dm2 && date2.isEndOfMoth) ||
        // e.g., Feb 28 -> Aug 30 ?
        (dm1 < dm2 && date1.isEndOfMoth)) {
        (date2.getYear - date1.getYear) + (date2.getMonthOfYear - date1.getMonthOfYear) / 12.0
      } else {
        fallBack.fraction(date1, date2, date3, freq)
      }
    }
  }

  def apply(): DayCountConvention = simpleImpl
}

