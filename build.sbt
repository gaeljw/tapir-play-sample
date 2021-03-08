val tapirVersion = "0.17.15"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """tapir-play-sample""",
    organization := "com.github.gaeljw",
    scalaVersion := "2.13.5",
    libraryDependencies ++= Seq(
      guice,
      ws,
      "net.codingwell"              %% "scala-guice"              % "4.2.11",
      "com.softwaremill.sttp.tapir" %% "tapir-core"               % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-play-server"        % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-json-play"          % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"       % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml" % tapirVersion,
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-play"    % tapirVersion,
      "org.scalatestplus.play"      %% "scalatestplus-play"       % "5.1.0" % Test
    ),
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))
  )

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.github.gaeljw.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.github.gaeljw.binders._"

// COMMANDS ALIASES

addCommandAlias("t", "test")
addCommandAlias("to", "testOnly")
addCommandAlias("tq", "testQuick")
addCommandAlias("tsf", "testShowFailed")

addCommandAlias("c", "compile")
addCommandAlias("tc", "test:compile")

addCommandAlias("f", "scalafmt")             // Format production files according to ScalaFmt
addCommandAlias("fc", "scalafmtCheck")       // Check if production files are formatted according to ScalaFmt
addCommandAlias("tf", "test:scalafmt")       // Format test files according to ScalaFmt
addCommandAlias("tfc", "test:scalafmtCheck") // Check if test files are formatted according to ScalaFmt
addCommandAlias("fmt", ";f;tf")              // Format all files according to ScalaFmt

// All the needed tasks before pushing to the repository (compile, compile test, format check in prod and test)
addCommandAlias("prep", ";c;tc;unitTests")
addCommandAlias("build", ";c;tc")
addCommandAlias("unitTests", ";to *.unitTests.*")
