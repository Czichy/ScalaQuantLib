logLevel := Level.Warn

resolvers ++= Seq(
  "Maven repo" at "http://repo1.maven.org/maven2",
  "Typesafe repo" at "http://repo.typesafe.com/typesafe/releases/"
)

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "3.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.3.0")
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

