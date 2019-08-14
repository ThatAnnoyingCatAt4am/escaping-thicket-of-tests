package com.github.thatannoyingcatat4am

import scala.util.Random

trait Config {
  def url: String
  val prop: Map[String, String]
}

class TestConfig extends Config {
  val dbName: String = Random.nextString(10)
  override val url: String = s"jdbc:h2:mem:$dbName;DB_CLOSE_DELAY=-1"
  override val prop: Map[String, String] = Map.empty
}
