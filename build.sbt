name := "registry"

ThisBuild / scalaVersion := "2.13.6"

lazy val allScalacOptions = Seq(
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-unchecked",
  "-Xcheckinit",
  "-Xlint:adapted-args",
  "-Ypatmat-exhaust-depth", "100", // Fixes: Exhaustivity analysis reached max recursion depth, not all missing cases are reported.
  "-Wconf:src=generated/.*:silent",
  "-Wconf:src=target/.*:silent", // silence the unused imports errors generated by the Play Routes
)

lazy val api = project
  .in(file("api"))
  .enablePlugins(PlayScala)
  .enablePlugins (Cinnamon)
  .enablePlugins(JavaAppPackaging, JavaAgent)
  .settings(commonSettings: _*)
  .settings(
    run / cinnamon := true,
    cinnamonLogLevel := "INFO",
    routesImport += "io.flow.registry.v0.Bindables._",
    routesGenerator := InjectedRoutesGenerator,
    javaAgents += "com.datadoghq" % "dd-java-agent" % "0.97.0",
    libraryDependencies ++= Seq(
      ws,
      guice,
      jdbc,
      "io.flow" %% "lib-postgresql-play-play28" % "0.4.54",
      "io.flow" %% "lib-metrics-play28" % "1.0.24",
      "com.typesafe.play" %% "play-json-joda" % "2.9.2",
      "org.postgresql" % "postgresql" % "42.3.3",
      "net.jcazevedo" %% "moultingyaml" % "0.4.2",
      "io.flow" %% "lib-test-utils-play28" % "0.1.69" % Test,
      "io.flow" %% "lib-usage-play28" % "0.1.89",
      "io.flow" %% "lib-log" % "0.1.65",
      "io.kamon" %% "kamon-datadog" % "2.5.0",
      Cinnamon.library.cinnamonAkka,
      Cinnamon.library.cinnamonCHMetrics,
      Cinnamon.library.cinnamonDatadog,
      Cinnamon.library.cinnamonDatadogSocket,
    ),
    scalacOptions ++= allScalacOptions,
  )

lazy val commonSettings: Seq[Setting[_]] = Seq(
  name ~= ("registry-" + _),
  libraryDependencies ++= Seq(
    specs2 % Test
  ),
  Compile / doc / sources := Seq.empty,
  Compile / packageDoc / publishArtifact := false,
  resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/",
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  resolvers += "Artifactory" at "https://flow.jfrog.io/flow/libs-release/",
  credentials += Credentials(
    "Artifactory Realm",
    "flow.jfrog.io",
    System.getenv("ARTIFACTORY_USERNAME"),
    System.getenv("ARTIFACTORY_PASSWORD")
  )
)
