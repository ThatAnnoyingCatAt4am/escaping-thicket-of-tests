package com.github.thatannoyingcatat4am

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object FutureOps {
  implicit class FutureOps[A](future: Future[A]) {
    def await: A = Await.result(future, 5.seconds)
  }
}
