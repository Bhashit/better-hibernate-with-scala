resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += Resolver.sonatypeRepo("snapshots")

// The Play plugin
addSbtPlugin("com.typesafe.play" %% "sbt-plugin" % "2.3.6")
