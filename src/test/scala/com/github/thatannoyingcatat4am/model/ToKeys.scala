package com.github.thatannoyingcatat4am.model

/**
 * Typeclass defining split a complex entity into a collection of keys
 * (relationships by ID)
 */
trait ToKeys[A] extends Convert[A, Set[Key]]

object ToKeys {
  def apply[T: ToKeys]: ToKeys[T] = implicitly[ToKeys[T]]

  implicit class ToKeysOps[T: ToKeys](a: T) {
    def toKeys: Set[Key] = ToKeys[T].convert(a)
  }

  implicit def seqToKeys[T: ToKeys]: ToKeys[Seq[T]] =
    (a: Seq[T]) => a.flatMap(_.toKeys).toSet

  implicit def listToKeys[T: ToKeys]: ToKeys[List[T]] =
    (a: List[T]) => a.flatMap(_.toKeys).toSet

  implicit val packageEntryToKeys: ToKeys[PackageRelationships] =
    (a: PackageRelationships) => Set(PackageKey(a.id, a.userId)) ++
      Set(UserKey(a.userId)) ++
      a.itemIds.map(PackageItemKey(_, a.id)).toSet ++
      a.bonusIds.map(BonusKey(_, a.id))
  implicit val dbStateToKeys: ToKeys[DbState] =
    (a: DbState) => a.entries.toKeys
}
