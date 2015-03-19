name := "ScalaQuantLib"

version := "0.5-snapshot"

scalaVersion := "2.11.6"

defaultScalariformSettings

lazy val jodaTimeDependencies = Seq("com.github.nscala-time" %% "nscala-time" % "1.8.0")

lazy val rxScala = Seq("io.reactivex" %% "rxscala" % "0.23.1")

libraryDependencies ++= jodaTimeDependencies ++ rxScala