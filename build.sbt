
name := "transactapp"

organization := "com.leadiq"

version := "1.0.0"

scalaVersion := "2.12.4"

scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaHttpVersion = "10.0.5"
  val twitterUtilCoreVersion = "18.2.0"
  val scalaTestVersion = "3.0.1"
  val scalaMockV = "3.5.0"

  Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,

    // https://mvnrepository.com/artifact/com.twitter/util-core
    "com.twitter" %% "util-collection" % twitterUtilCoreVersion,

    "com.typesafe.play" %% "play-json" % "2.6.0-M6",

    "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
    "org.scalamock" %% "scalamock-scalatest-support" % scalaMockV % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test
  )
}

lazy val root = project.in(file(".")).configs(IntegrationTest)
Defaults.itSettings

Revolver.settings

enablePlugins(JavaAppPackaging)

// run scalastyle at compile time
lazy val compileScalastyle = taskKey[Unit]("compileScalastyle")

compileScalastyle := org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value

(compile in Compile) <<= (compile in Compile) dependsOn compileScalastyle

// code coverage configuration
coverageEnabled := false

coverageHighlighting := true

coverageMinimum := 100

coverageFailOnMinimum := true

parallelExecution in Test := false
