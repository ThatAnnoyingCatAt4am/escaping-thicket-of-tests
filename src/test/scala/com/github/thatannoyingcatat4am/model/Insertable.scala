package com.github.thatannoyingcatat4am.model

import com.github.thatannoyingcatat4am.BaseTestDatabaseSupport.DbFixture
import slick.jdbc.{H2Profile, JdbcProfile}
import scala.concurrent.ExecutionContext

/**
 * Typeclass defining how to insert an object into a database
 * (returning a slick's DBIO)
 */
trait Insertable[A] {
  def insert(a: A): DbFixture
}
object Insertable {
  import slick.dbio.DBIO

  implicit val profile: JdbcProfile = H2Profile
  implicit val ec: ExecutionContext = ExecutionContext.global
  import profile.api._

  def apply[A: Insertable]: Insertable[A] = implicitly[Insertable[A]]

  implicit class InsertableOps[A : Insertable](a: A) {
    def insert: DbFixture = Insertable[A].insert(a)
  }

  implicit val packageInsertable: Insertable[PackageRow] =
    (a: PackageRow) =>
      sqlu"""INSERT INTO PACKAGE (ID, NAME, USER_ID, STATUS) VALUES (#${a.id}, '#${a.name}', #${a.userId}, '#${a.status}')""".map(_ => None)

  implicit val packageItemInsertable: Insertable[PackageItemRow] =
    (a: PackageItemRow) =>
      sqlu"""INSERT INTO PACKAGE_ITEM (ID, PACKAGE_ID, NAME, PRICE) VALUES (#${a.id}, #${a.packageId}, '#${a.name}', #${a.price})""".map(_ => None)

  implicit val userInsertable: Insertable[UserRow] =
    (a: UserRow) =>
      sqlu"""INSERT INTO USER (ID, NAME, ROLE) VALUES (#${a.id}, '#${a.name}', '#${a.role}')""".map(_ => None)

  implicit val bonusInsertable: Insertable[BonusRow] =
    (a: BonusRow) =>
      sqlu"""INSERT INTO BONUS_FOR_PACKAGE (ID, PACKAGE_ID, BONUS_AMOUNT) VALUES (#${a.id}, #${a.packageId}, #${a.bonusAmount})""".map(_ => None)

  implicit val dbContentsInsertable: Insertable[Set[Row]] = (rows: Set[Row]) =>
    DBIO.seq(
      rows.map {
        case r: PackageRow => r.insert
        case r: PackageItemRow => r.insert
        case r: UserRow => r.insert
        case r: BonusRow => r.insert
      }.toSeq: _*
    ) >> DBIO.successful(None)
}
