val scala3Version = "3.6.2"

lazy val root = project
  .in(file("."))
  .settings(
    // Project
    name := "spaceinvaders42",
    version := "0.1.0",
    scalaVersion := scala3Version,
    // --

    // Test framework
    libraryDependencies += "org.scalameta" %% "munit" % "1.0.0" % Test,
    // Cats
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.7",
    // fs2
    libraryDependencies += "co.fs2" %% "fs2-core" % "3.9.3",
    // Scalafx
    libraryDependencies += "org.scalafx" %% "scalafx" % "22.0.0-R33",
    libraryDependencies ++= {
      // Determine OS version of JavaFX binaries
      lazy val osName = System.getProperty("os.name") match {
        case n if n.startsWith("Linux")   => "linux"
        case n if n.startsWith("Mac")     => "mac"
        case n if n.startsWith("Windows") => "win"
        case _ => throw new Exception("Unknown platform!")
      }
      Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
        .map(m => "org.openjfx" % s"javafx-$m" % "17" classifier osName)
    },
    // Enable forking for JavaFX runtime
    fork := true
  )
