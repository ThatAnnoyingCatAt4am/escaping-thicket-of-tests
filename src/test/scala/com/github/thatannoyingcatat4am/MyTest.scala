package com.github.thatannoyingcatat4am

import com.github.thatannoyingcatat4am.model._
import BaseTestDatabaseSupport.DbFixture
import org.scalatest.Assertion
import org.scalatest.prop.Tables._
import org.slf4j.Logger
import slick.jdbc.JdbcBackend.Database
import scala.concurrent.Future

class MyTest extends MyTestDetails {
  val dataTable: Seq[DataRow] = Table(
    ("Package ID", "Customer's role", "Item prices", "Bonus value", "Expected final price")
    , (1, "customer", Vector(40, 20, 30)           , Vector.empty      ,  90.0)
    , (2, "customer", Vector(250)                  , Vector.empty      , 225.0)
    , (3, "customer", Vector(100, 120, 30)         , Vector(40)        , 210.0)
    , (4, "customer", Vector(100, 120, 30, 100)    , Vector(20, 20)    , 279.0)
    , (5, "vip"     , Vector(100, 120, 30, 100, 50), Vector(10, 20, 10), 252.0)
  )

  "If the buyer's role is" - {
    "a customer" - {
      "And the total price of items" - {
        "< 250 after applying bonuses - no discount" - {
          "(case: no bonuses)" in calculatePriceFor(dataTable, 1)
          "(case: has bonuses)" in calculatePriceFor(dataTable, 3)
        }
        ">= 250 after applying bonuses" - {
          "If there are no bonuses - 10% off on the subtotal" in
            calculatePriceFor(dataTable, 2)
          "If there are bonuses - 10% off on the subtotal after applying bonuses" in
            calculatePriceFor(dataTable, 4)
        }
      }
    }
    "a vip - then they get a 20% off before applying bonuses and then all the other rules apply" in
      calculatePriceFor(dataTable, 5)
  }
}

trait MyTestDetails extends TestSettings with Overrides {
  type DataRow = (Int, String, Vector[Int], Vector[Int], Double)

  def makeState(row: DataRow): Logger => DbFixture = {
    val items: Map[Int, Int] = ((1 to row._3.length) zip row._3).toMap
    val bonuses: Map[Int, Int] = ((1 to row._4.length) zip row._4).toMap
    MyFixtures.makeFixture(
      state = PackageRelationships
        .minimal(id = row._1, userId = 1)
        .withItems(items.keys)
        .withBonuses(bonuses.keys),
      overrides = changeRole(userId = 1, newRole = row._2) andThen
        items.map { case (id, newPrice) => changePrice(id, newPrice) }.foldPls andThen
        bonuses.map { case (id, newBonus) => changeBonus(id, newBonus) }.foldPls
    )
  }
  def runProductionCode(id: Int): Database => Double =
    (db: Database) => new SomeProductionLogic(db).calculatePrice(id)
  def checkResult(expected: Double): Double => Future[Assertion] =
    (result: Double) => result shouldBe expected

  def calculatePriceFor(table: Seq[DataRow], idx: Int): FixtureParam => Future[Assertion] =
    testInDb(
      state = makeState(table.row(idx)),
      execute = runProductionCode(table.row(idx)._1),
      check = checkResult(table.row(idx)._5)
    )
}