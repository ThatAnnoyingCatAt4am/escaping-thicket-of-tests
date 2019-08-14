package com.github.thatannoyingcatat4am.model

import com.github.thatannoyingcatat4am.BaseTestDatabaseSupport._
import com.github.thatannoyingcatat4am.FixtureCompilationSupport

object MyFixtures
  extends FixtureCompilationSupport[DbState, Key, Row, DbFixture, Set] {

  import Enrich._
  import Insertable._
  import ToKeys._

  override def extractKeys: DbState => Set[Key] = _.toKeys

  override def convertKeyToRows: Key => Set[Row] = _.enrich

  override def buildFixture: Set[Row] => DbFixture = _.insert

  override def deduplicateKeys: Set[Key] => Set[Key] = x => x
}
