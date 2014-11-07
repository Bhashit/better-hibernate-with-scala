name := """hibernate-property-access"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.2"

resolvers += Resolver.sonatypeRepo("snapshots")

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  "org.hibernate" % "hibernate-entitymanager" % "4.3.6.Final",
  "org.aspectj" % "aspectjweaver" % "1.8.2",
  "org.aspectj" % "aspectjrt"  % "1.8.2",
  "org.slf4j" % "slf4j-api" % "1.7.7",
  "ch.qos.logback" % "logback-core" % "1.1.2",
  "org.scala-lang" % "scala-reflect" % "2.11.2"
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-encoding", "UTF-8"
)
