package com.github.thatannoyingcatat4am.model

import com.softwaremill.quicklens._

trait Overrides {
  implicit class IterableOverrideOps(a: Iterable[Set[Row] => Set[Row]]) {
    def foldPls: Set[Row] => Set[Row] = a.foldLeft((x: Set[Row]) => x)(_ andThen _)
  }

  def changeRole(userId: Int, newRole: String): Set[Row] => Set[Row] =
    _.modifyAll(_.each.when[UserRow])
      .using(r => if (r.id == userId) r.modify(_.role).setTo(newRole) else r)
  def changePrice(itemId: Int, newPrice: Int): Set[Row] => Set[Row] =
    _.modifyAll(_.each.when[PackageItemRow])
      .using(r => if (r.id == itemId) r.modify(_.price).setTo(newPrice) else r)
  def changeBonus(bonusId: Int, newBonus: Int): Set[Row] => Set[Row] =
    _.modifyAll(_.each.when[BonusRow])
      .using(r => if (r.id == bonusId) r.modify(_.bonusAmount).setTo(newBonus) else r)
}
