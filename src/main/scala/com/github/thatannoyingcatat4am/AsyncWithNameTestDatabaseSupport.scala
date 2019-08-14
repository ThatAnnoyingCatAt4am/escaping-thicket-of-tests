package com.github.thatannoyingcatat4am

import org.scalatest.{Assertion, TestData}
import org.slf4j.Logger
import BaseTestDatabaseSupport.{CheckDb, DbFixture}
import TestLifecycleSupport.CheckAsync
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

trait AsyncWithNameTestDatabaseSupport[R]
  extends TestLifecycleSupport with BaseTestDatabaseSupport[R] {

  /** A version of the test lifecycle function for testing on a database */
  def testInDb[A: Logged](
    state: Logger => DbFixture,
    execute: R => A,
    check: CheckAsync[A],
    dbCheck: CheckDb = noCheckDb
  )(implicit ec: ExecutionContext,
    profile: JdbcProfile): TestData => Future[Assertion] = { testData =>

    implicit val logger: Logger = makeLogger(testData)
    containerDb { db: Database =>
      runTestCycle(
        state(logger), prepareDatabase(profile, db, dbCheck), execute, check
      )
    }
  }
}