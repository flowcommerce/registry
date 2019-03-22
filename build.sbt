import play.sbt.PlayScala._

name := "registry"

scalaVersion in ThisBuild := "2.12.8"

lazy val api = project
  .in(file("api"))
  .enablePlugins(PlayScala)
  .enablePlugins(NewRelic)
  .enablePlugins(JavaAppPackaging, JavaAgent)
  .settings(commonSettings: _*)
  .settings(
    routesImport += "io.flow.registry.v0.Bindables._",
    routesGenerator := InjectedRoutesGenerator,
    javaAgents += "org.aspectj" % "aspectjweaver" % "1.8.13",
    javaOptions in Universal += "-Dorg.aspectj.tracing.factory=default",
    javaOptions in Test += "-Dkamon.modules.kamon-system-metrics.auto-start=false",
    javaOptions in Test += "-Dkamon.show-aspectj-missing-warning=no",
    libraryDependencies ++= Seq(
      ws,
      guice,
      jdbc,
      "io.flow" %% "lib-postgresql-play-play26" % "0.3.5",
      "io.flow" %% "lib-play-graphite-play26" % "0.0.83",
      "com.typesafe.play" %% "play-json-joda" % "2.6.10",
      "org.postgresql" % "postgresql" % "42.2.5",
      "net.jcazevedo" %% "moultingyaml" % "0.4.0",
      "io.flow" %% "lib-test-utils" % "0.0.37" % Test,
      "io.flow" %% "lib-usage" % "0.0.78",
      "io.flow" %% "lib-log" % "0.0.64",
      compilerPlugin("com.github.ghik" %% "silencer-plugin" % "1.3.0"),
      "com.github.ghik" %% "silencer-lib" % "1.3.0" % Provided
    ),
    // silence all warnings on autogenerated files
    scalacOptions += "-P:silencer:pathFilters=target/.*;app/generated/.*",
    // Make sure you only exclude warnings for the project directories, i.e. make builds reproducible
    scalacOptions += s"-P:silencer:sourceRoots=${baseDirectory.value.getCanonicalPath}"
  )

lazy val commonSettings: Seq[Setting[_]] = Seq(
  name ~= ("registry-" + _),
  libraryDependencies ++= Seq(
    specs2 % Test
  ),
  sources in(Compile, doc) := Seq.empty,
  publishArtifact in(Compile, packageDoc) := false,
  resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  resolvers += "Artifactory" at "https://flow.jfrog.io/flow/libs-release/",
  credentials += Credentials(
    "Artifactory Realm",
    "flow.jfrog.io",
    System.getenv("ARTIFACTORY_USERNAME"),
    System.getenv("ARTIFACTORY_PASSWORD")
  )
)
version := "0.4.0"
version := "0.4.27"
