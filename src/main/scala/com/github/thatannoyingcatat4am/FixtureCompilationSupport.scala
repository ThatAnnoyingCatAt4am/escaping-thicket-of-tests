package com.github.thatannoyingcatat4am

import cats.FlatMap
import org.slf4j.Logger
import scala.language.higherKinds

/**
  * @tparam S State
  * @tparam K Key
  * @tparam R Row
  * @tparam FX Fixture
  * @tparam F Collection
  */
trait FixtureCompilationSupport[S, K, R, FX, F[_]] extends LoggingSupport {

  def makeFixtureWithLogger(
    state: S,
    applyOverrides: F[R] => F[R] = x => x
  )(implicit logger: Logger, flatMap: FlatMap[F], logRule: Logged[F[R]]): FX =

    (extractKeys andThen
      deduplicateKeys andThen
      enrichWithSampleData andThen
      applyOverrides andThen
      logged andThen
      buildFixture) (state)

  def makeFixture(
    state: S,
    overrides: F[R] => F[R] = x => x
  )(implicit flatMap: FlatMap[F], logRule: Logged[F[R]]): Logger => FX = { implicit logger =>

    makeFixtureWithLogger(state, overrides)
  }

  def getDatabaseContents(
    input: S,
    applyOverrides: F[R] => F[R] = x => x
  )(implicit flatMap: FlatMap[F]): F[R] =

    (extractKeys andThen
      deduplicateKeys andThen
      enrichWithSampleData andThen
      applyOverrides) (input)

  private def enrichWithSampleData(implicit flatMap: FlatMap[F]): F[K] => F[R] =
    FlatMap[F].flatMap(_)(convertKeyToRows)

  def extractKeys: S => F[K]

  def convertKeyToRows: K => F[R]

  def buildFixture: F[R] => FX

  def deduplicateKeys: F[K] => F[K]
}
