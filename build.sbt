name := """tapir-play-sample"""
organization := "com.github.gaeljw"

scalaVersion := "3.8.4"

scalacOptions ++= Seq(
  "-release:25"
)

val tapirVersion = "1.13.32"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    publishArtifact := false
  )
  .settings(
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))
  )
  .settings(

    libraryDependencies += ("com.fasterxml.jackson" % "jackson-bom" % "2.22.3").pomOnly(),
    libraryDependencies += guice,
    libraryDependencies += ws,
    
    // Tapir
    libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-core" % tapirVersion,
    libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-play-server" % tapirVersion,

    // JSON handling (you can use Circe instead of Play-JSON)
    libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-json-play" % tapirVersion,

    // Tapir OpenAPI
    libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % tapirVersion,

    // Swagger UI
    // You can also host Swagger UI by yourself and get rid of this dependency with more manual setup
    libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-bundle" % tapirVersion,

    libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.2" % Test,

    // For Akka Streams (if using streaming)
    libraryDependencies += "com.softwaremill.sttp.shared" %% "pekko" % "1.5.3",
  )

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.github.gaeljw.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.github.gaeljw.binders._"
