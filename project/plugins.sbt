// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play"%"sbt-plugin"%"2.6.20")

addSbtPlugin("com.gilt.sbt" % "sbt-newrelic" % "0.2.4")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")

addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.4")
