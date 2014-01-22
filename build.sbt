name := "scala-statsd"

version := "1.0.00"

organization := "com.zestia.statsd"

crossScalaVersions := Seq("2.10.2")

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.7" % "test",
  "org.specs2" %% "specs2" % "2.1.1" % "test"
)

parallelExecution in Test := false

testOptions in Test += Tests.Argument("junitxml","console")