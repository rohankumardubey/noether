/*
 * Copyright 2018 Spotify AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.spotify.noether

import org.scalactic.TolerantNumerics
import org.scalactic.Equality

class ClassificationReportTest extends AggregatorTest {
  implicit private val doubleEq: Equality[Double] = TolerantNumerics.tolerantDoubleEquality(0.1)

  it should "return correct scores" in {
    val data = List(
      (0.1, false),
      (0.1, true),
      (0.4, false),
      (0.6, false),
      (0.6, true),
      (0.6, true),
      (0.8, true)
    ).map { case (s, pred) => Prediction(pred, s) }

    val score = run(ClassificationReport())(data)

    assert(score.recall === 0.75)
    assert(score.precision === 0.75)
    assert(score.fscore === 0.75)
    assert(score.fpr === 0.333)
  }

  it should "support multiclass reports" in {
    val predictions = Seq(
      (0, 0),
      (0, 0),
      (0, 1),
      (1, 1),
      (1, 1),
      (1, 0),
      (1, 2),
      (2, 2),
      (2, 2),
      (2, 2)
    ).map { case (p, a) => Prediction(a, p) }

    val reports = run(MultiClassificationReport(Seq(0, 1, 2)))(predictions)

    val report0 = reports(0)
    assert(report0.recall == 2.0 / 3.0)
    assert(report0.precision == 2.0 / 3.0)
    val report1 = reports(1)
    assert(report1.recall == 2.0 / 3.0)
    assert(report1.precision == 0.5)
    val report2 = reports(2)
    assert(report2.recall == 0.75)
    assert(report2.precision == 1.0)
  }
}
