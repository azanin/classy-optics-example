import Dependencies._

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "com.example"
ThisBuild / organizationName := "example"

lazy val catsMeowMtl = "com.olegpy" %% "meow-mtl-core" % "0.4.0"
lazy val catseffect = "org.typelevel" %% "cats-effect" % "2.1.3"

lazy val root = (project in file("."))
  .settings(
    name := "classyoptics-examples",
    libraryDependencies ++= Seq(
      compilerPlugin(
        "org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full
      ),
      compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    ),
    libraryDependencies ++= Seq(catseffect, catsMeowMtl, scalaTest % Test)
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
// Compiler plugins
