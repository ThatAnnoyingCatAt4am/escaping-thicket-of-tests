name := "article"

version := "0.1"

scalaVersion := "2.12.9"

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.8")

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.25",
  "org.slf4j" % "jul-to-slf4j" % "1.7.25",
  "ch.qos.logback" % "logback-classic" % "1.1.6",
  "com.h2database" % "h2" % "1.4.197",
  "org.flywaydb" % "flyway-core" % "5.2.0",

  "com.typesafe.slick" %% "slick" % "3.2.3",
  "org.scalatest" %% "scalatest" % "3.0.5",
  "org.typelevel" %% "cats-core" % "1.4.0",
  "com.softwaremill.quicklens" %% "quicklens" % "1.4.8"
)
