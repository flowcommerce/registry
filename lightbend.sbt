ThisBuild / resolvers += "lightbend-commercial-mvn" at "https://repo.lightbend.com/pass/_JbGD6Myl8YJcSlv3tIGUhSpI7xPd4vv4tI1AUfJUGakTlcY/commercial-releases"
ThisBuild / resolvers += Resolver.url(
  "lightbend-commercial-ivy",
  url("https://repo.lightbend.com/pass/_JbGD6Myl8YJcSlv3tIGUhSpI7xPd4vv4tI1AUfJUGakTlcY/commercial-releases")
)(Resolver.ivyStylePatterns)
