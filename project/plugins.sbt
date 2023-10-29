addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.20")
addSbtPlugin("com.github.sbt" % "sbt-release" % "1.1.0")
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.2")

// * org.scala-lang.modules:scala-xml_2.12:2.1.0 (early-semver) is selected over {1.2.0, 1.1.1}
// [error] 	    +- org.scala-lang:scala-compiler:2.12.17              (depends on 2.1.0)
// [error] 	    +- com.typesafe.sbt:sbt-native-packager:1.5.2 (scalaVersion=2.12, sbtVersion=1.0) (depends on 1.1.1)
// [error] 	    +- com.typesafe.play:twirl-api_2.12:1.5.1             (depends on 1.2.0)
ThisBuild / libraryDependencySchemes += "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always