package org.scalaquant.math

import org.scalaquant.math.Constants._

object ErrorFunction {

  private val tiny = QL_EPSILON

  private val one =  1.00000000000000000000e+00

  private val erx =  8.45062911510467529297e-01
  private val efx  =  1.28379167095512586316e-01
  private val efx8 =  1.02703333676410069053e+00

  private val pp0  =  1.28379167095512558561e-01
  private val pp1  = -3.25042107247001499370e-01
  private val pp2  = -2.84817495755985104766e-02
  private val pp3  = -5.77027029648944159157e-03
  private val pp4  = -2.37630166566501626084e-05

  private val qq1  =  3.97917223959155352819e-01
  private val qq2  =  6.50222499887672944485e-02
  private val qq3  =  5.08130628187576562776e-03
  private val qq4  =  1.32494738004321644526e-04
  private val qq5  = -3.96022827877536812320e-06

  private val pa0  = -2.36211856075265944077e-03
  private val pa1  =  4.14856118683748331666e-01
  private val pa2  = -3.72207876035701323847e-01
  private val pa3  =  3.18346619901161753674e-01
  private val pa4  = -1.10894694282396677476e-01
  private val pa5  =  3.54783043256182359371e-02
  private val pa6  = -2.16637559486879084300e-03

  private val qa1  =  1.06420880400844228286e-01
  private val qa2  =  5.40397917702171048937e-01
  private val qa3  =  7.18286544141962662868e-02
  private val qa4  =  1.26171219808761642112e-01
  private val qa5  =  1.36370839120290507362e-02
  private val qa6  =  1.19844998467991074170e-02

  private val ra0  = -9.86494403484714822705e-03
  private val ra1  = -6.93858572707181764372e-01
  private val ra2  = -1.05586262253232909814e+01
  private val ra3  = -6.23753324503260060396e+01
  private val ra4  = -1.62396669462573470355e+02
  private val ra5  = -1.84605092906711035994e+02
  private val ra6  = -8.12874355063065934246e+01
  private val ra7  = -9.81432934416914548592e+00

  private val sa1  =  1.96512716674392571292e+01
  private val sa2  =  1.37657754143519042600e+02
  private val sa3  =  4.34565877475229228821e+02
  private val sa4  =  6.45387271733267880336e+02
  private val sa5  =  4.29008140027567833386e+02
  private val sa6  =  1.08635005541779435134e+02
  private val sa7  =  6.57024977031928170135e+00
  private val sa8  = -6.04244152148580987438e-02

  private val rb0  = -9.86494292470009928597e-03
  private val rb1  = -7.99283237680523006574e-01
  private val rb2  = -1.77579549177547519889e+01
  private val rb3  = -1.60636384855821916062e+02
  private val rb4  = -6.37566443368389627722e+02
  private val rb5  = -1.02509513161107724954e+03
  private val rb6  = -4.83519191608651397019e+02

  private val sb1  =  3.03380607434824582924e+01
  private val sb2  =  3.25792512996573918826e+02
  private val sb3  =  1.53672958608443695994e+03
  private val sb4  =  3.19985821950859553908e+03
  private val sb5  =  2.55305040643316442583e+03
  private val sb6  =  4.74528541206955367215e+02
  private val sb7  = -2.24409524465858183362e+01

  def apply(x: Double): Double = Math.abs(x) match {
      case ax if ax < 0.84375 => /* |x|<0.84375 */
        if (ax < 3.7252902984e-09) { /* |x|<2**-28 */
          if (ax < Double.MaxValue * 16) 0.125*(8.0*x+efx8*x) else x + efx*x
        } else {
          val z = x*x
          val r = pp0+z*(pp1+z*(pp2+z*(pp3+z*pp4)))
          val s = one+z*(qq1+z*(qq2+z*(qq3+z*(qq4+z*qq5))))
          val y = r/s
          x + x * y
        }
      case ax if ax < 1.25 => /* 0.84375 <= |x| < 1.25 */
        val s = ax-one
        val P = pa0+s*(pa1+s*(pa2+s*(pa3+s*(pa4+s*(pa5+s*pa6)))))
        val Q = one+s*(qa1+s*(qa2+s*(qa3+s*(qa4+s*(qa5+s*qa6)))))
        if (x>=0)  erx + P/Q else  -erx - P/Q
      case ax if ax >= 6 =>  /* inf>|x|>=6 */
        if (x>=0) one-tiny else tiny-one
      case ax => /* Starts to lose accuracy when ax~5 */
        val s = one/(ax*ax)
        val (rr, ss) = if (ax < 2.85714285714285) {
          /* |x| < 1/0.35 */
          (ra0+s*(ra1+s*(ra2+s*(ra3+s*(ra4+s*(ra5+s*(ra6+s*ra7)))))),
           one+s*(sa1+s*(sa2+s*(sa3+s*(sa4+s*(sa5+s*(sa6+s*(sa7+s*sa8))))))))
        } else  {
          /* |x| >= 1/0.35 */
          (rb0+s*(rb1+s*(rb2+s*(rb3+s*(rb4+s*(rb5+s*rb6))))),
           one+s*(sb1+s*(sb2+s*(sb3+s*(sb4+s*(sb5+s*(sb6+s*sb7)))))))
        }
        val r = Math.exp( -ax*ax-0.5625 + rr / ss)
        if (x >= 0) one-r/ax else  r/ax-one
      }
}
