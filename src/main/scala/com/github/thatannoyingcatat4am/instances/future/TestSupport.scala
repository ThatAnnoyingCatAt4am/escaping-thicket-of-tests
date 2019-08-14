package com.github.thatannoyingcatat4am.instances.future

import org.scalatest.Assertion
import org.slf4j.Logger
import com.github.thatannoyingcatat4am.{Logged, TestLifecycleSupport}
import scala.concurrent.Future

trait TestSupport extends TestLifecycleSupport {
  def test[A: Logged](
    execute: Unit => A,
    check: A => Future[Assertion]
  )(implicit logger: Logger): Future[Assertion] =
    super.runTest[A, Future](execute, check)

  def testOnMocks[A: Logged](
    sideEffectingMock: => Unit,
    execute: Unit => A,
    check: A => Future[Assertion]
  )(implicit logger: Logger): Future[Assertion] =
    super.runTestOnMocks[A, Future](sideEffectingMock, execute, check)
}
object TestSupport extends TestSupport
