ThisBuild / resolvers += "lightbend-commercial-mvn" at "https://repo.lightbend.com/pass/W3HITqlg5_LzhSmgIsGagTMhHksIgyAPjlZShEp-dcRiAzK6/commercial-releases"
ThisBuild / resolvers += Resolver.url(
  "lightbend-commercial-ivy",
  url("https://repo.lightbend.com/pass/W3HITqlg5_LzhSmgIsGagTMhHksIgyAPjlZShEp-dcRiAzK6/commercial-releases")
)(Resolver.ivyStylePatterns)
