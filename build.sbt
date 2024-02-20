name := "registry"

ThisBuild / scalaVersion := "2.13.10"
ThisBuild / javacOptions ++= Seq("-source", "17", "-target", "17")

lazy val allScalacOptions = Seq(
  "-deprecation",
  "-feature",
  "-Xfatal-warnings",
  "-unchecked",
  "-Xcheckinit",
  "-Xlint:adapted-args",
  "-Ypatmat-exhaust-depth",
  "100", // Fixes: Exhaustivity analysis reached max recursion depth, not all missing cases are reported.
  "-Wconf:src=generated/.*:silent",
  "-Wconf:src=target/.*:silent", // silence the unused imports errors generated by the Play Routes
)

lazy val api = project
  .in(file("api"))
  .enablePlugins(PlayScala)
  .enablePlugins(JavaAppPackaging, JavaAgent)
  .settings(commonSettings: _*)
  .settings(
    scalafmtOnCompile := true,
    routesImport += "io.flow.registry.v0.Bindables._",
    routesGenerator := InjectedRoutesGenerator,
    javaAgents += "com.datadoghq" % "dd-java-agent" % "1.28.0",
    libraryDependencies ++= Seq(
      ws,
      "com.google.inject" % "guice" % "5.1.0",
      "com.google.inject.extensions" % "guice-assistedinject" % "5.1.0",
      "org.projectlombok" % "lombok" % "1.18.30" % "provided",
      jdbc,
      "io.flow" %% "lib-postgresql-play-play28" % "0.5.37",
      "io.flow" %% "lib-metrics-play28" % "1.0.79",
      "com.typesafe.play" %% "play-json-joda" % "2.9.4",
      "org.postgresql" % "postgresql" % "42.7.1",
      "net.jcazevedo" %% "moultingyaml" % "0.4.2",
      "io.flow" %% "lib-test-utils-play28" % "0.2.24" % Test,
      "io.flow" %% "lib-usage-play28" % "0.2.38",
      "io.flow" %% "lib-log" % "0.2.14",
    ),
    Test / javaOptions ++= Seq(
      "--add-exports=java.base/sun.security.x509=ALL-UNNAMED",
      "--add-opens=java.base/sun.security.ssl=ALL-UNNAMED",
    ),
    scalacOptions ++= allScalacOptions,
  )

lazy val commonSettings: Seq[Setting[_]] = Seq(
  name ~= ("registry-" + _),
  libraryDependencies ++= Seq(
    "io.flow" %% "lib-healthcheck-play28" % "0.0.20",
    specs2 % Test,
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
    System.getenv("ARTIFACTORY_PASSWORD"),
  ),
)
