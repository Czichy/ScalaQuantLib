package org.scalaquant.core.common.time

import org.scalaquant.core.common.time.TimeUnit._
import org.scalatest.{FunSuite, Matchers, FlatSpec}
import org.scalaquant.core.common.time.Period

class DayCountFunSuite extends FunSuite {


  test("Testing simple day counter...") {
      val p = List(Period(3,Months), Period(6,Months), Period(1,Years))
      val expected = List(0.25, 0.5, 1.0)

      // 4 years should be enough
//      Date first(1,January,2002), last(31,December,2005);
//      DayCounter dayCounter = SimpleDayCounter();
//
//      for (Date start = first; start <= last; start++) {
//        for (Size i=0; i<n; i++) {
//          Date end = start + p[i];
//          Time calculated = dayCounter.yearFraction(start,end);
//          if (std::fabs(calculated-expected[i]) > 1.0e-12) {
//            BOOST_FAIL("from " << start << " to " << end << ":\n"
//              << std::setprecision(12)
//              << "    calculated: " << calculated << "\n"
//              << "    expected:   " << expected[i]);
//          }
//        }
      }

    }

    //  "Composite Quote" should "be able to composited from SimpleQuote" in {
    //
    //  }
  }
