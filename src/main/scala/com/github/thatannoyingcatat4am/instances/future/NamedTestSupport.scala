package com.github.thatannoyingcatat4am.instances.future

import org.scalatest.{Assertion, TestData}
import org.slf4j.Logger
import com.github.thatannoyingcatat4am.{Logged, TestLifecycleSupport}
import scala.concurrent.Future

/**
 * Test lifecycle functions extended for logging
 */
trait NamedTestSupport extends TestLifecycleSupport {
  def simpleCheck[A](
    check: Unit => Future[Assertion]
  ): TestData => Future[Assertion] = { testData =>
    implicit val logger: Logger = makeLogger(testData)
    super.runTest((_: Unit) => (), check)
  }

  def test[A: Logged](
    test: Unit => A,
    check: A => Future[Assertion]
  ): TestData => Future[Assertion] = { testData =>
    implicit val logger: Logger = makeLogger(testData)
    super.runTest[A, Future](test, check)
  }

  def testOnMocks[A: Logged](
    sideEffectingMock: => Unit,
    test: Unit => A,
    check: A => Future[Assertion]
  ): TestData => Future[Assertion] = { testData =>
    implicit val logger: Logger = makeLogger(testData)
    super.runTestOnMocks[A, Future](sideEffectingMock, test, check)
  }
}

object NamedTestSupport extends NamedTestSupport
