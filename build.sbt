import play.PlayImport.PlayKeys._
import scoverage.ScoverageSbtPlugin.ScoverageKeys._

name := "registry"

scalaVersion in ThisBuild := "2.11.8"

// required because of issue between scoverage & sbt
parallelExecution in Test in ThisBuild := true

lazy val generated = project
  .in(file("generated"))
  .enablePlugins(PlayScala)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      ws
    )
  )

lazy val api = project
  .in(file("api"))
  .dependsOn(generated)
  .aggregate(generated)
  .enablePlugins(PlayScala)
  .settings(commonSettings: _*)
  .settings(
    routesImport += "io.flow.registry.v0.Bindables._",
    routesGenerator := InjectedRoutesGenerator,
    libraryDependencies ++= Seq(
      ws,
      jdbc,
      "io.flow" %% "lib-play" % "0.1.5",
      "io.flow" %% "lib-postgresql" % "0.0.26",
      "com.typesafe.play" %% "anorm" % "2.5.0",
      "org.postgresql" % "postgresql" % "9.4.1208",
      "org.scalatestplus" %% "play" % "1.4.0" % "test"
    )
  )

lazy val commonSettings: Seq[Setting[_]] = Seq(
  name <<= name("registry-" + _),
  libraryDependencies ++= Seq(
    specs2 % Test,
    "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  ),
  sources in (Compile,doc) := Seq.empty,
  publishArtifact in (Compile, packageDoc) := false,
  scalacOptions += "-feature",
  coverageHighlighting := true,
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
version := "0.0.23"
