name := "ScalaQuantLib"
version := "0.5-snapshot"
scalaVersion := "2.11.7"

lazy val akka = Seq("com.typesafe.akka" %% "akka-actor" % "2.4.1")
lazy val scalaTest = Seq("org.scalatest" %% "scalatest" % "2.2.4" % "test")

libraryDependencies ++=  akka ++ scalaTest
