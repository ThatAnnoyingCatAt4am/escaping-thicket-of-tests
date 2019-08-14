package com.github.thatannoyingcatat4am

import com.softwaremill.quicklens._

package object model {
  // This describes relationships between entities in our database
  // having Package as the base entity
  case class PackageRelationships(
    id: Int,
    userId: Int,
    // One-to-many relationships
    itemIds: Seq[Int] = Seq.empty,
    bonusIds: Seq[Int] = Seq.empty
    // Many-to-many relationships are achievable by storing tuples
    // or just straight-up keys
    // manyToMany: Seq[(Int, Int)] = Seq.empty
  ) {
    def withItems(itemIds: Seq[Int]): PackageRelationships =
      this.modify(_.itemIds).setTo(itemIds)
    def withBonuses(bonusIds: Seq[Int]): PackageRelationships =
      this.modify(_.bonusIds).setTo(bonusIds)
  }
  object PackageRelationships {
    def minimal(id: Int = 1, userId: Int = 1) = PackageRelationships(id, userId)
  }

  // Class representing a complete fixture - this is what we're going
  // to transform our dataset into
  case class DbState(entries: Seq[PackageRelationships])

  sealed trait Key
  case class PackageKey(id: Int, userId: Int) extends Key
  case class PackageItemKey(id: Int, packageId: Int) extends Key
  case class UserKey(id: Int) extends Key
  case class BonusKey(id: Int, packageId: Int) extends Key

  sealed trait Row
  case class PackageRow(id: Int, userId: Int, name: String, status: String) extends Row
  case class PackageItemRow(id: Int, packageId: Int, name: String, price: Int) extends Row
  case class UserRow(id: Int, name: String, role: String) extends Row
  case class BonusRow(id: Int, packageId: Int, bonusAmount: Int) extends Row
}
