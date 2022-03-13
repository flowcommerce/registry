// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.13")

addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.2")

addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.5")

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.22")

resolvers += "Flow Plugins" at "https://flow.jfrog.io/flow/plugins-release/"

addSbtPlugin("io.flow" % "sbt-flow-linter" % "0.0.34")

addSbtPlugin("com.lightbend.cinnamon" % "sbt-cinnamon" % "2.16.2")
