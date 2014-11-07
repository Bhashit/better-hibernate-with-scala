name := """project-neumann"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.2"

resolvers += Resolver.sonatypeRepo("snapshots")


libraryDependencies ++= Seq(
  jdbc,
  "org.hibernate" % "hibernate-entitymanager" % "4.3.6.Final",
  "com.zaxxer" % "HikariCP-java6" % "2.0.1", // the -java6 is for both java 6 and 7.
  "mysql" % "mysql-connector-java" % "5.1.33",
  "org.scaldi" %% "scaldi-play" % "0.4.1",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "ws.securesocial" %% "securesocial" % "master-SNAPSHOT",
  "org.aspectj" % "aspectjweaver" % "1.8.2",
  "org.aspectj" % "aspectjrt"  % "1.8.2",
  cache,
  ws
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-encoding", "UTF-8"
)
