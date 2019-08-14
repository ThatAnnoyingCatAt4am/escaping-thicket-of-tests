package com.github.thatannoyingcatat4am

import org.slf4j.Logger

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

trait Logged[A] {
  def log(a: A)(implicit logger: Logger): A
}

object Logged {
  def apply[A: Logged]: Logged[A] = implicitly[Logged[A]]

  implicit class LoggedOps[A : Logged](a: A) {
    def log(implicit logger: Logger): A = Logged[A].log(a)
  }

  implicit def futureLogged[A]: Logged[Future[A]] = new Logged[Future[A]] {
    override def log(a: Future[A])(implicit logger: Logger): Future[A] = {
      implicit val ec: ExecutionContextExecutor = ExecutionContext.global
      a.map { t =>
        logger.info(t.toString)
        t
      }
    }
  }

  implicit def anyNoLogged[A]: Logged[A] = new Logged[A] {
    override def log(a: A)(implicit logger: Logger): A = {
      logger.info(s"Unable to log the result of this type, please see LoggingSupport")
      a
    }
  }
}

trait LoggingSupport {
  import Logged._

  def logged[A : Logged](implicit logger: Logger): A => A = { a =>
    a.log
    a
  }
}

object LoggingSupport extends LoggingSupport
