import play.sbt.PlayScala._

name := "registry"

scalaVersion in ThisBuild := "2.12.5"

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
      "io.flow" %% "lib-postgresql-play-play26" % "0.1.83",
      "io.flow" %% "lib-play-graphite-play26" % "0.0.16",
      "com.typesafe.play" %% "play-json-joda" % "2.6.9",
      "org.postgresql" % "postgresql" % "42.2.2",
      "net.jcazevedo" %% "moultingyaml" % "0.4.0",
      "io.flow" %% "lib-test-utils" % "0.0.7" % Test,
      "io.flow" %% "lib-usage" % "0.0.3"
    )
  )

lazy val commonSettings: Seq[Setting[_]] = Seq(
  name ~= ("registry-" + _),
  libraryDependencies ++= Seq(
    specs2 % Test
  ),
  sources in(Compile, doc) := Seq.empty,
  publishArtifact in(Compile, packageDoc) := false,
  scalacOptions += "-feature",
  resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
  resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases",
  resolvers += "Artifactory" at "https://flow.artifactoryonline.com/flow/libs-release/",
  credentials += Credentials(
    "Artifactory Realm",
    "flow.artifactoryonline.com",
    System.getenv("ARTIFACTORY_USERNAME"),
    System.getenv("ARTIFACTORY_PASSWORD")
  )
)
version := "0.2.47"
