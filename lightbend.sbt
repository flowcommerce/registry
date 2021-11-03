ThisBuild / resolvers += "lightbend-commercial-mvn" at "https://repo.lightbend.com/pass/pt7bwqhZQApVzeydQV6JC_p5JCXsVoZqok6WhMPNEU7Gfiwf/commercial-releases"
ThisBuild / resolvers += Resolver.url(
  "lightbend-commercial-ivy",
  url("https://repo.lightbend.com/pass/pt7bwqhZQApVzeydQV6JC_p5JCXsVoZqok6WhMPNEU7Gfiwf/commercial-releases")
)(Resolver.ivyStylePatterns)