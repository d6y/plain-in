name := "plain sql IN clause"

version := "3.3"

scalaVersion := "2.13.3"

scalacOptions ++= Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-unchecked",
  "-feature",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-Ywarn-dead-code",
  "-Xlint"
)

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick"          % "3.3.3",
  "com.h2database"     % "h2"              % "1.4.200",
  "ch.qos.logback"     % "logback-classic" % "1.2.3"
)
