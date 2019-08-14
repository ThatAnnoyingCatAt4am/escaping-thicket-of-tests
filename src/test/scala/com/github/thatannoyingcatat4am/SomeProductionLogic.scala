package com.github.thatannoyingcatat4am

import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile

class SomeProductionLogic(db: Database)(implicit profile: JdbcProfile) {
  import profile.api._
  import FutureOps._

  def calculatePrice(id: Int): Double = {
    val (role, price) = db.run {
      sql"""SELECT U.ROLE, SUM(PI.PRICE) FROM PACKAGE P
           |JOIN PACKAGE_ITEM PI ON PI.PACKAGE_ID = P.ID
           |JOIN USER U ON P.USER_ID = U.ID
           |WHERE P.ID = $id
           |GROUP BY P.ID, U.ID
         """.stripMargin.as[(String, Int)]
    }.await.head
    val bonus = db.run {
      sql"""SELECT SUM(B.BONUS_AMOUNT) FROM PACKAGE P
           |LEFT JOIN BONUS_FOR_PACKAGE B ON B.PACKAGE_ID = P.ID
           |WHERE P.ID = $id
           |GROUP BY P.ID
         """.stripMargin.as[Int]
    }.await.head

    val basePriceDiscount = role match {
      case "vip" => 0.8
      case _ => 1.0
    }
    val priceAfterDiscount = price * basePriceDiscount - bonus
    val sumDiscount = if (priceAfterDiscount >= 250) 0.9 else 1.0
    val finalPrice = priceAfterDiscount * sumDiscount
    finalPrice
  }
}
