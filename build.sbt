name := """tapir-play-sample"""
organization := "com.github.gaeljw"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    publishArtifact := false
  )

publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))

scalaVersion := "3.5.2"

val tapirVersion = "1.11.7"
val jacksonVersion = "2.18.0"

libraryDependencies += guice
libraryDependencies += ws

// Tapir
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-play-server" % tapirVersion

// JSON handling (you can use Circe instead of Play-JSON)
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-play" % tapirVersion

// Tapir OpenAPI
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion

// Swagger UI
// You can also host Swagger UI by yourself and get rid of this dependency with more manual setup
libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test

// For Akka Streams (if using streaming)
libraryDependencies += "com.softwaremill.sttp.shared" %% "pekko" % "1.4.2"

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.github.gaeljw.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.github.gaeljw.binders._"

// Enforce Jackson consistency
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-annotations" % jacksonVersion
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion
dependencyOverrides += "com.fasterxml.jackson.dataformat" % "jackson-dataformat-cbor" % jacksonVersion
dependencyOverrides += "com.fasterxml.jackson.datatype" % "jackson-datatype-jdk8" % jacksonVersion
dependencyOverrides += "com.fasterxml.jackson.datatype" % "jackson-datatype-jsr310" % jacksonVersion
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-parameter-names" % jacksonVersion
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-paranamer" % jacksonVersion
dependencyOverrides += "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion
