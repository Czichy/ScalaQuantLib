name := "ScalaQuantLib"
version := "0.5-snapshot"
scalaVersion := "2.11.6"

//defaultScalariformSettings

lazy val jodaTime = Seq("com.github.nscala-time" %% "nscala-time" % "2.0.0")
lazy val scalaTest = Seq("org.scalatest" % "scalatest_2.11" % "2.2.4" % "test")

libraryDependencies ++= jodaTime ++ scalaTest
