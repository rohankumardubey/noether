/*
 * Copyright 2018 Spotify AB.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import pl.project13.scala.sbt.JmhPlugin
import sbt.Keys._

val breezeVersion = "1.0-RC2"
val algebirdVersion = "0.13.4"
val scalaTestVersion = "3.0.5"

val commonSettings = Seq(
  organization := "com.spotify",
  name := "noether",
  description := "ML Aggregators",
  scalaVersion := "2.12.7",
  crossScalaVersions := Seq("2.11.12", scalaVersion.value),
  scalacOptions ++= commonScalacOptions,
  scalacOptions in (Compile, console) --= Seq("-Xfatal-warnings"),
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint:unchecked"),
  javacOptions in (Compile, doc) := Seq("-source", "1.8"),
  publishTo := Some(if (isSnapshot.value) {
    Opts.resolver.sonatypeSnapshots
  } else {
    Opts.resolver.sonatypeStaging
  }),
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  sonatypeProfileName := "com.spotify",
  licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt")),
  homepage := Some(url("https://github.com/spotify/noether")),
  scmInfo := Some(
    ScmInfo(url("https://github.com/spotify/noether.git"),
            "scm:git:git@github.com:spotify/noether.git")),
  developers := List(
    Developer(id = "rwhitcomb",
              name = "Richard Whitcomb",
              email = "richwhitjr@gmail.com",
              url = url("https://twitter.com/rwhitcomb")),
    Developer(id = "fallonfofallon",
              name = "Fallon Chen",
              email = "fallon@spotify.com",
              url = url("https://twitter.com/fallonfofallon")),
    Developer(id = "regadas",
              name = "Filipe Regadas",
              email = "filiperegadas@gmail.com",
              url = url("https://twitter.com/regadas"))
  )
)

lazy val root: Project = project
  .in(file("."))
  .settings(commonSettings)
  .settings(publish / skip := true)
  .aggregate(noetherCore, noetherExamples, noetherBenchmark)

lazy val noetherCore: Project = project
  .in(file("core"))
  .settings(commonSettings)
  .settings(mimaSettings("noether-core"))
  .settings(
    name := "noether-core",
    moduleName := "noether-core",
    description := "Machine Learning Aggregators",
    libraryDependencies ++= Seq(
      "org.scalanlp" %% "breeze" % breezeVersion,
      "com.twitter" %% "algebird-core" % algebirdVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion
    ),
    fork in Test := true
  )

lazy val noetherBenchmark = project
  .in(file("benchmark"))
  .settings(JmhPlugin.projectSettings: _*)
  .settings(commonSettings)
  .settings(
    publish / skip := true,
    coverageExcludedPackages := "com\\.spotify\\.noether\\.benchmark.*"
  )
  .dependsOn(noetherCore)
  .enablePlugins(JmhPlugin)

lazy val noetherExamples: Project = project
  .in(file("examples"))
  .settings(commonSettings)
  .settings(
    name := "noether-examples",
    moduleName := "noether-examples",
    description := "Noether Examples",
    publish / skip := true
  )
  .dependsOn(noetherCore)

// sampled from https://tpolecat.github.io/2017/04/25/scalac-flags.html
lazy val commonScalacOptions = Seq(
  "-target:jvm-1.8",
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xfuture", // Turn on future language features.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match", // Pattern match may not be typesafe.
  "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification", // Enable partial unification in type constructor inference
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
)

// based on the nice https://github.com/typelevel/cats/blob/master/build.sbt#L208
def mimaSettings(moduleName: String): Seq[Def.Setting[Set[sbt.ModuleID]]] = {
  import sbtrelease.Version
  // Safety Net for Exclusions
  lazy val excludedVersions: Set[String] = Set()
  // Safety Net for Inclusions
  lazy val extraVersions: Set[String] = Set()

  def semverBinCompatVersions(major: Int, minor: Int, patch: Int): Set[(Int, Int, Int)] = {
    val majorVersions: List[Int] = List(major)
    val minorVersions: List[Int] =
      if (major >= 1) {
        Range(0, minor).inclusive.toList
      } else {
        List(minor)
      }

    def patchVersions(currentMinVersion: Int): List[Int] =
      if (minor == 0 && patch == 0) {
        List.empty[Int]
      } else {
        if (currentMinVersion != minor) {
          List(0)
        } else {
          Range(0, patch - 1).inclusive.toList
        }
      }

    val versions = for {
      maj <- majorVersions
      min <- minorVersions
      pat <- patchVersions(min)
    } yield (maj, min, pat)
    versions.toSet
  }

  def mimaVersions(version: String): Set[String] =
    Version(version) match {
      case Some(Version(major, Seq(minor, patch), _)) =>
        semverBinCompatVersions(major.toInt, minor.toInt, patch.toInt)
          .map { case (maj, min, pat) => s"${maj}.${min}.${pat}" }
      case _ =>
        Set.empty[String]
    }

  Seq(
    mimaPreviousArtifacts := (mimaVersions(version.value) ++ extraVersions)
      .diff(excludedVersions)
      .map(v => "com.spotify" %% moduleName % v))
}
