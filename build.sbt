name := """project-neumann"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

resolvers += Resolver.sonatypeRepo("snapshots")


libraryDependencies ++= Seq(
  jdbc,
  "org.hibernate" % "hibernate-entitymanager" % "5.0.1.Final",
  "org.hibernate" % "hibernate-spatial" % "5.0.1.Final",
  "com.zaxxer" % "HikariCP" % "2.4.1",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "org.json4s" %% "json4s-native" % "3.2.10",
  "org.json4s" %% "json4s-jackson" % "3.2.10",
  "ws.securesocial" %% "securesocial" % "master-SNAPSHOT",
  "org.aspectj" % "aspectjweaver" % "1.8.2",
  "org.aspectj" % "aspectjrt"  % "1.8.2",
  cache,
  ws
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-encoding", "UTF-8"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

fork in run := true

connectInput in run := true

// The steps of the asset pipeline (used in stage and dist tasks)
pipelineStages := Seq(digest, gzip)

pipelineStages in Assets := Seq(digest, gzip)

// exclude the third party files from the linting
includeFilter in (Assets, JshintKeys.jshint) := new FileFilter {
  def accept(f: File) = {
    s"""app/assets/javascripts/src/""".r.findFirstIn(f.getAbsolutePath).isDefined
  }
}

