name := """tapir-play-sample"""
organization := "com.github.gaeljw"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    publishArtifact := false
  )

publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))

scalaVersion := "2.13.10"

val tapirVersion = "1.4.0"

libraryDependencies += guice

// Tapir
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-play-server" % tapirVersion

// JSON handling (you can use Circe instead of Play-JSON)
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-play" % tapirVersion

// Tapir OpenAPI
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion
libraryDependencies += "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml" % "0.3.2"

// Swagger UI for Play
// You can also host Swagger UI by yourself and get rid of this dependency
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui" % tapirVersion

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test

// For Akka Streams (if using streaming)
libraryDependencies += "com.softwaremill.sttp.shared" %% "akka" % "1.3.15"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.github.gaeljw.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.github.gaeljw.binders._"
