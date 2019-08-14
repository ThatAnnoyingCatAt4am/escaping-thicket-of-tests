package com.github.thatannoyingcatat4am

import cats.Id
import org.scalatest.{Assertion, TestData}
import org.scalatest.Matchers._
import org.slf4j.{Logger, LoggerFactory}
import TestLifecycleSupport.CheckAsync
import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait TestLifecycleSupport extends LoggingSupport {
  def noCheckAsync(implicit ec: ExecutionContext): CheckAsync[Future[_]] =
    _.flatMap(_ => Future.successful(true shouldEqual true))

  val noFixture: Unit = Unit
  val noPreparation: Unit => Unit = identity[Unit]

  protected def makeLogger(td: TestData): Logger =
    LoggerFactory.getLogger(
      s"""${this.getClass.getName}
         |-> ${td.name}""".stripMargin
    )

  /**
    * The basic test lifecycle function.
    * @tparam FX Fixture
    * @tparam D Dependency
    * @tparam A Result
    * @tparam F Collection (e.g. Future/Id)
    */
  protected def runTestCycle[FX, D, A : Logged, F[_]](
    fixture: FX,
    prepare: FX => D,
    execute: D => A,
    check: A => F[Assertion]
  )(implicit logger: Logger): F[Assertion] =

    (prepare andThen
      execute andThen
      logged andThen
      check) (fixture)

  /** A simpler version for when a fixture is just a function that sets up mocks */
  protected def runTestOnMocks[A : Logged, F[_]](
    sideEffectingMock: => Unit,
    execute: Unit => A,
    check: A => F[Assertion]
  )(implicit logger: Logger): F[Assertion] = runTestCycle(
    noFixture, (_: Unit) => sideEffectingMock, execute, check
  )

  /** A simpler version for testing without dependencies (e.g. unit-testing a pure function) */
  protected def runTest[A : Logged, F[_]](
    execute: Unit => A,
    check: A => F[Assertion]
  )(implicit logger: Logger): F[Assertion] = runTestCycle(
    noFixture, noPreparation, execute, check
  )
}

object TestLifecycleSupport extends TestLifecycleSupport {
  type CheckAsync[A] = A => Future[Assertion]
  type Check[A] = A => Id[Assertion]
  type SimpleCheckAsync = CheckAsync[Unit]
}
