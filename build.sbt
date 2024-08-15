name := """scala-sbt-play-blog-app"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.14"

// Dependency override to resolve conflict
dependencyOverrides += "org.scala-lang.modules" %% "scala-xml" % "2.2.0"

// Play Framework dependencies
libraryDependencies += guice
libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.1.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0"

// Slick dependencies
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.4.0"
libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.4.0"

// PostgreSQL JDBC driver
libraryDependencies += "org.postgresql" % "postgresql" % "42.6.0"

// Testing dependencies
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % Test
