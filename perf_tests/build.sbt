enablePlugins(GatlingPlugin)

scalaVersion := "2.12.7"
name := "perf_tests"
version := "0.3.2"

scalacOptions := Seq(
  "-encoding", "UTF-8", "-target:jvm-1.8", "-deprecation",
  "-feature", "-unchecked", "-language:implicitConversions", "-language:postfixOps")

libraryDependencies += "io.gatling.highcharts" % "gatling-charts-highcharts" % "3.0.0" % "test"
libraryDependencies += "io.gatling" % "gatling-test-framework" % "3.0.0" % "test"
