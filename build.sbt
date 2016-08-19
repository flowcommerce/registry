import play.PlayImport.PlayKeys._

name := "registry"

scalaVersion in ThisBuild := "2.11.8"

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
      jdbc,
      "io.flow" %% "lib-play" % "0.1.57",
      "io.flow" %% "lib-postgresql" % "0.0.35",
      "org.postgresql" % "postgresql" % "9.4.1209",
      "org.scalatestplus" %% "play" % "1.4.0" % "test",
      "net.jcazevedo" %% "moultingyaml" % "0.2"
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
