name := "registry"

ThisBuild / scalaVersion := "2.13.5"

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
  .enablePlugins(JavaAppPackaging, JavaAgent)
  .settings(commonSettings: _*)
  .settings(
    Universal / javaOptions ++= Seq(
      "-Ddd.profiling.enabled=true",
      s"-Ddd.service=registry",
    ),
    routesImport += "io.flow.registry.v0.Bindables._",
    routesGenerator := InjectedRoutesGenerator,
//    javaAgents += "io.kamon" % "kanela-agent" % "1.0.11",
    javaAgents += "com.datadoghq" % "dd-java-agent" % "0.82.0",
    libraryDependencies ++= Seq(
      ws,
      guice,
      jdbc,
      "io.flow" %% "lib-postgresql-play-play28" % "0.4.26",
      "io.flow" %% "lib-play-graphite-play28" % "0.1.97",
      "com.typesafe.play" %% "play-json-joda" % "2.9.2",
      "org.postgresql" % "postgresql" % "42.2.22",
      "net.jcazevedo" %% "moultingyaml" % "0.4.2",
      "io.flow" %% "lib-test-utils-play28" % "0.1.36" % Test,
      "io.flow" %% "lib-usage-play28" % "0.1.64",
      "io.flow" %% "lib-log" % "0.1.43",
      "io.kamon" %% "kamon-datadog" % "2.2.1",
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
