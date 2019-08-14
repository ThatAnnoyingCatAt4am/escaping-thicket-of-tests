package com.github.thatannoyingcatat4am.model

/**
 * Typeclass defining how to fill an object with fake data
 */
trait Enrich[A] extends Convert[A, Set[Row]]
object Enrich {
  def apply[T: Enrich]: Enrich[T] = implicitly[Enrich[T]]

  implicit class EnrichOps[T: Enrich](a: T) {
    def enrich: Set[Row] = Enrich[T].convert(a)
  }

  implicit val enrichKey: Enrich[Key] = {
    case PackageKey(id, userId) => Set(
      PackageRow(id, userId, SampleData.name, SampleData.status)
    )
    case PackageItemKey(id, packageId) => Set(
      PackageItemRow(id, packageId, SampleData.name, SampleData.price)
    )
    case UserKey(id) => Set(UserRow(id, SampleData.name, SampleData.role))
    case BonusKey(id, packageId) => Set(BonusRow(id, packageId, SampleData.bonusAmount))
  }
}
