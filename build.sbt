name := "ScalaQuantLib"
version := "0.5-snapshot"
scalaVersion := "2.11.6"

//defaultScalariformSettings

lazy val scalaz = Seq("org.scalaz" %% "scalaz-core" % "7.1.3")
lazy val akka = Seq("com.typesafe.akka" %% "akka-actor" % "2.3.12")
lazy val jodaTime = Seq("com.github.nscala-time" %% "nscala-time" % "2.0.0")
lazy val scalaTest = Seq("org.scalatest" %% "scalatest" % "2.2.4" % "test")

libraryDependencies ++= jodaTime ++ akka ++ scalaz ++ scalaTest
