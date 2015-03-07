package org.scalaquant.core.common.time.daycounts

/**
 * Created by neo on 2015-03-02.
 */

import org.joda.time.LocalDate

import scala.language.implicitConversions
import org.scalaquant.core.common.time.JodaDateTimeHelper._

trait Thirty360 extends DayCountConvention {

  protected def mm2Mod(d1: Int, d2: Int, m: Int): Int
  protected def d1Mod(d1: Int, d2: Int, m: Int): Int
  protected def d2Mod(d1: Int, d2: Int, m: Int): Int

  override def dayCount(date1: LocalDate, date2: LocalDate): Int = {
    val (yy1, mm1, dd1) = date1.YMD
    val (yy2, mm2, dd2) = date2.YMD

    val M2 = mm2Mod(dd1, dd2, mm2)
    val D1 = d1Mod(dd1, dd2, mm1)
    val D2 = d2Mod(dd1, dd2, mm2)

    360 * (yy2 - yy1) + 30 * (M2 - mm1 - 1) + Math.max(0, 30 - D1) + Math.min(30, D2)
  }
  override def fraction(date1: LocalDate, date2: LocalDate): Double = {
    dayCount(date1, date2) / 360.0
  }
}

object Thirty360 {
  trait Convention

  object Convention {
    case object USA extends Convention
    case object BondBasis extends Convention
    case object European extends Convention
    case object EurobondBasis extends Convention
    case object Italian extends Convention
  }

  private val USImplement = new Thirty360 {
    override def name: String = "30/360 (Bond Basis)"
    override def mm2Mod(d1: Int, d2: Int, m: Int): Int = if (d2 == 31 && d1 < 30) m + 1 else m
    override def d1Mod(d1: Int, d2: Int, m: Int): Int = d1
    override def d2Mod(d1: Int, d2: Int, m: Int): Int = if (d2 == 31 && d1 < 30) 1 else d2
  }
  private val EUImplement = new Thirty360 {
    override def name: String = "30E/360 (Eurobond Basis)"
    override def mm2Mod(d1: Int, d2: Int, m: Int): Int = m
    override def d1Mod(d1: Int, d2: Int, m: Int): Int = d1
    override def d2Mod(d1: Int, d2: Int, m: Int): Int = d2
  }
  private val ITImplement = new Thirty360 {
    override def name: String = "30/360 (Italian)"
    override def mm2Mod(d1: Int, d2: Int, m: Int): Int = m
    override def d1Mod(d1: Int, d2: Int, m: Int): Int = if (m == 2 && d1 > 27) 30 else d1
    override def d2Mod(d1: Int, d2: Int, m: Int): Int = if (m == 2 && d1 > 27) 30 else d2
  }

  def apply(convention: Convention = Convention.BondBasis): Thirty360 = {
    convention match {
      case Convention.USA | Convention.BondBasis => USImplement
      case Convention.European | Convention.EurobondBasis => EUImplement
      case Convention.Italian => ITImplement
    }
  }
}

object SimpleDayCountConvention extends DayCountConvention {
  override def name: String = "Simple"
  private val fallBack = Thirty360()
  override def dayCount(date1: LocalDate, date2: LocalDate): Int = fallBack.dayCount(date1, date2)
  override def fraction(date1: LocalDate, date2: LocalDate): Double = {
    val dm1 = date1.getDayOfMonth
    val dm2 = date2.getDayOfMonth

    if (dm1 == dm2 ||
      // e.g., Aug 30 -> Feb 28 ?
      (dm1 > dm2 && date2.isEndOfMoth) ||
      // e.g., Feb 28 -> Aug 30 ?
      (dm1 < dm2 && date1.isEndOfMoth)) {
      (date2.getYear - date1.getYear) + (date2.getMonthOfYear - date1.getMonthOfYear) / 12.0
    } else {
      fallBack.fraction(date1, date2)
    }
  }
}

