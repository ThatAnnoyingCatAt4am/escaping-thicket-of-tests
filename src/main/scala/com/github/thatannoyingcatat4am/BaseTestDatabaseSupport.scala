package com.github.thatannoyingcatat4am

import cats.~>
import org.scalatest.{Assertion, Matchers}
import BaseTestDatabaseSupport.{CheckDb, DbFixture}
import slick.dbio.DBIO
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

case class SuccessfulRollback[T](result: T) extends Exception

/**
  * @tparam R - Database Runner (Something that has DBIO[A] => Future[A])
  */
trait BaseTestDatabaseSupport[R] extends Matchers {

  val emptyFixture: DbFixture = DBIO.successful(None)

  val noCheckDb: CheckDb = DBIO.successful(true shouldEqual true)

  def containerDb(testCode: Database => Future[Assertion]): Future[Assertion]

  def prepareDatabase(profile: JdbcProfile, db: Database, dbCheck: CheckDb)
    (implicit ec: ExecutionContext): DbFixture => R

  protected def addFixture[A](fixture: DbFixture)(implicit ec: ExecutionContext)
  :DBIO ~> DBIO =
    λ[DBIO ~> DBIO] { originalAction =>
      for {
        _ <- fixture
        result <- originalAction
      } yield result
    }

  protected def addDbCheck(selectAndCheck: CheckDb)
    (implicit ec: ExecutionContext): DBIO ~> DBIO =
    λ[DBIO ~> DBIO] { originalAction =>
      for {
        result <- originalAction
        _ <- selectAndCheck
      } yield result
    }

  protected def addRollback(implicit ec: ExecutionContext): DBIO ~> DBIO =
    λ[DBIO ~> DBIO] { originalAction =>
      for {
        result <- originalAction
        _ <- DBIO.failed(SuccessfulRollback(result))
      } yield result
    }
}
object BaseTestDatabaseSupport {
  type DbFixture = DBIO[Option[Int]]
  type CheckDb = DBIO[Assertion]
}
