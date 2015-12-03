package org.scalaquant.math.solver1d

/**
  * Created by neo on 11/20/15.
  */
object NewtonSafe extends Impl{

  def solveImpl(f: (Double) => Double, xAccuracy: Double): Double = {
    var (froot, dfroot, dx, dxold) = (0.0,0.0,0.0,0.0)
    var xh, xl = (0.0, 0.0)

    // Orient the search so that f(xl) < 0
    if (fxMin_ < 0.0) {
      xl = xMin_;
      xh = xMax_;
    } else {
      xh = xMin_;
      xl = xMax_;
    }

    // the "stepsize before last"
    dxold = xMax_-xMin_;
    // it was dxold=std::fabs(xMax_-xMin_); in Numerical Recipes
    // here (xMax_-xMin_ > 0) is verified in the constructor

    // and the last step
    dx = dxold;

    froot = f(root_);
    dfroot = f.derivative(root_);
    QL_REQUIRE(dfroot != Null<Real>(),
      "NewtonSafe requires function's derivative");
    ++evaluationNumber_;

    while (evaluationNumber_<=maxEvaluations_) {
      // Bisect if (out of range || not decreasing fast enough)
      if ((((root_-xh)*dfroot-froot)*
        ((root_-xl)*dfroot-froot) > 0.0)
        || (std::fabs(2.0*froot) > std::fabs(dxold*dfroot))) {

        dxold = dx;
        dx = (xh-xl)/2.0;
        root_=xl+dx;
      } else {
        dxold = dx;
        dx = froot/dfroot;
        root_ -= dx;
      }
      // Convergence criterion
      if (std::fabs(dx) < xAccuracy) {
        f(root_);
        ++evaluationNumber_;
        return root_;
      }
      froot = f(root_);
      dfroot = f.derivative(root_);
      ++evaluationNumber_;
      if (froot < 0.0)
        xl=root_;
      else
        xh=root_;
    }

    QL_FAIL("maximum number of function evaluations ("
      << maxEvaluations_ << ") exceeded");
  }
}
