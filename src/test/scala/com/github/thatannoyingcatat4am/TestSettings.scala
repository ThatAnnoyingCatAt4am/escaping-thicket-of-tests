package com.github.thatannoyingcatat4am

import cats.FlatMap
import com.github.thatannoyingcatat4am.model.{DbState, PackageRelationships, Row}
import com.github.thatannoyingcatat4am.BaseTestDatabaseSupport.{CheckDb, DbFixture}
import org.flywaydb.core.Flyway
import org.scalatest.{fixture, Assertion}
import org.slf4j.Logger
import slick.jdbc.{H2Profile, JdbcBackend, JdbcProfile}
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.{ExecutionContext, Future}

import scala.language.implicitConversions


trait TestSettings extends fixture.AsyncFreeSpec
  with fixture.AsyncTestDataFixture
  with AsyncWithNameTestDatabaseSupport[Database] {

  import FutureOps._

  implicit val profile: JdbcProfile = H2Profile

  // Some syntactic sugar
  implicit def packageEntry2DbState(entry: PackageRelationships): DbState = DbState(Seq(entry))
  implicit def iterable2Seq[A](iter: Iterable[A]): Seq[A] = iter.toSeq
  implicit class TableOps[A](table: Seq[A]) {
    def row(row: Int): A = table.apply(row - 1)
  }

  // This is where we set up a DB and pass it to the test code
  override def containerDb(
    testCode: JdbcBackend.Database => Future[Assertion]
  ): Future[Assertion] = {
    val config = new TestConfig
    val db: Database = Database.forURL(config.url, config.prop)
    migrateDb(config)
    testCode(db)
  }

  // Used in testInDb, this is where a fixture actually gets inserted to a DB
  override def prepareDatabase(
    profile: JdbcProfile,
    db: JdbcBackend.Database,
    dbCheck: CheckDb
  )(implicit ec: ExecutionContext): DbFixture => Database = { fixture =>
    db.run(fixture).await
    db
  }

  private def migrateDb(config: Config): Unit =
    Flyway
      .configure()
      .dataSource(config.url, "", "")
      .load()
      .migrate()

  // Defines the way database fixture is logged
  implicit def rowsLogged: Logged[Set[Row]] = new Logged[Set[Row]] {
    override def log(a: Set[Row])(implicit logger: Logger): Set[Row] = {
      logger.info(s"DB = ${a.toString}")
      a
    }
  }

  implicit val flatMapSet: FlatMap[Set] = new FlatMap[Set] {
    override def flatMap[A, B](fa: Set[A])(f: A => Set[B]): Set[B] = fa.flatMap(f)

    override def tailRecM[A, B](a: A)(f: A => Set[Either[A, B]]): Set[B] = ???

    override def map[A, B](fa: Set[A])(f: A => B): Set[B] = fa.map(f)
  }
}
