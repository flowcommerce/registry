import play.sbt.PlayScala._

name := "registry"

scalaVersion in ThisBuild := "2.11.12"

crossScalaVersions := Seq("2.11.12", "2.12.4")

lazy val api = project
  .in(file("api"))
  .enablePlugins(PlayScala)
  .enablePlugins(NewRelic)
  .settings(commonSettings: _*)
  .settings(
    routesImport += "io.flow.registry.v0.Bindables._",
    routesGenerator := InjectedRoutesGenerator,
    libraryDependencies ++= Seq(
      ws,
      guice,
      jdbc,
      "io.flow" %% "lib-postgresql-play" % "0.1.47-play26",
      "com.typesafe.play" %% "play-json-joda" % "2.6.8",
      "org.postgresql" % "postgresql" % "42.1.4",
      "net.jcazevedo" %% "moultingyaml" % "0.4.0",
      "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
      "io.flow" %% "lib-test-utils" % "0.0.1" % Test
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
version := "0.2.6"
