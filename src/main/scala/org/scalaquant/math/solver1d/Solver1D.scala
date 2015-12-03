package org.scalaquant.math.solver1d

import org.scalaquant.math.Constants

/**
  * Created by neo on 11/17/15.
  */
trait Impl {
  def solveImpl(f : Double => Double, xAccuracy: Double): Double
}

abstract class Solver1D[T: Impl](maxEvaluations: Int,
                        lowerBound: Double,
                        upperBound: Double,
                        lowerBoundEnforced: Boolean = false,
                        upperBoundEnforced: Boolean = false)(implicit impl: T) {

  private def check(accuracy: Double) = require(accuracy>0.0, s"accuracy ($accuracy) must be positive")

  def apply(f: Double => Double, accuracy: Double, guess: Double, step: Double) = {
    check(accuracy)

    var accuracyPromximation = Math.max(accuracy, Constants.QL_EPSILON)

    var growthFactor = 1.6
    var flipflop = -1

    var root = guess
    var fxMax = f(root)

    // monotonically crescent bias, as in optionValue(volatility)
    fxMax compareTo 0.0 {
      case 1 =>
        var xMin = ;
        var fxMin = f(enforceBounds(root - step))
        val xMax = root
      case -1 =>
        var Min = root
        var xMin = fxMax
        var Max = enforceBounds(root + step)
        var xMax = f(xMax)
      case _ => root
    }


    var evaluationNumber = 2
    while (evaluationNumber <= maxEvaluations) {
      if (fxMin * fxMax <= 0.0) {
        if (fxMin ~= 0.0)) xMin_;
        if (fxMax, ~= 0.0)) xMax_;
        root_ = (xMax_+xMin_)/2.0;
        return impl.solveImpl(f, accuracy)
      }
      if (Math.abs(fxMin) < Math.abs(fxMax)) {
        xMin_ = enforceBounds_(xMin_+growthFactor*(xMin_ - xMax_));
        fxMin_= f(xMin)
      } else if (std::fabs(fxMin_) > std::fabs(fxMax_)) {
        xMax_ = enforceBounds_(xMax_+growthFactor*(xMax_ - xMin_));
        fxMax_= f(xMax)
      } else if (flipflop == -1) {
        xMin_ = enforceBounds_(xMin_+growthFactor*(xMin_ - xMax_));
        fxMin_= f(xMin)
        evaluationNumber_++;
        flipflop = 1;
      } else if (flipflop == 1) {
        xMax_ = enforceBounds_(xMax_+growthFactor*(xMax_ - xMin_));
        fxMax_= f(xMax)
        flipflop = -1;
      }

    }

  }

  def apply(function: Double => Double, accuracy: Double, guess: Double, xmin: Double, xmax: Double) = {
    check(accuracy)

  }
}
