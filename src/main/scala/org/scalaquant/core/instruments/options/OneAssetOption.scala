package org.scalaquant.core.instruments.options

import org.scalaquant.core.common.Exercise
import org.scalaquant.core.instruments.payoffs.Payoff
import org.scalaquant.core.pricingengines.PricingEngine

/**
 * Created by neo on 2015-03-13.
 */


class OneAssetOption(payoff: Payoff, exercise: Exercise) extends Option[OneAssetOption.Results](payoff, exercise){

  protected var _delta: Double = 0.0
  protected var _gamma: Double = 0.0
  protected var _theta: Double = 0.0
  protected var _vega: Double = 0.0
  protected var _rho: Double = 0.0
  protected var _dividendRho: Double = 0.0

  protected var _itmCashProbability: Double = 0.0
  protected var _deltaForward: Double = 0.0
  protected var _elasticity: Double = 0.0
  protected var _thetaPerDay: Double = 0.0
  protected var _strikeSensitivity: Double = 0.0

  def delta: Double = _delta
  def gamma: Double = _gamma
  def theta: Double = _theta
  def vega: Double = _vega
  def rho: Double = _rho
  def dividendRho: Double = _dividendRho
  def itmCashProbability: Double = _itmCashProbability
  def deltaForward: Double = _deltaForward
  def elasticity: Double = _elasticity
  def thetaPerDay: Double = _thetaPerDay
  def strikeSensitivity: Double = _strikeSensitivity

  override def onNext(value: OneAssetOption.Results): Unit = {
    _delta = value.delta
    _gamma = value.gamma
    _theta = value.theta
    _vega = value.vega
    _rho = value.rho
    _dividendRho = value.dividendRho

    _itmCashProbability = value.itmCashProbability
    _deltaForward = value.deltaForward
    _elasticity = value.elasticity
    _thetaPerDay = value.thetaPerDay
    _strikeSensitivity = value.strikeSensitivity
  }

  override def onError(error: Throwable): Unit =  {
    _delta = Double.NaN
    _gamma = Double.NaN
    _theta = Double.NaN
    _vega = Double.NaN
    _rho = Double.NaN
    _dividendRho = Double.NaN

    _itmCashProbability = Double.NaN
    _deltaForward = Double.NaN
    _elasticity = Double.NaN
    _thetaPerDay = Double.NaN
    _strikeSensitivity = Double.NaN
  }

}

object OneAssetOption{
  trait Results extends PricingEngine.Results with Option.Greeks with Option.MoreGreeks
}