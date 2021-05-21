// *****************************************************************************
// Build settings
// *****************************************************************************
inThisBuild(
  Seq(
    scalaVersion       := library.Version.scala,
    crossScalaVersions := Seq(scalaVersion.value, library.Version.scala),
    organization       := "$organization$",
    name               := "$name$",
    
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding",
      "UTF-8",
      "-feature",
      "-Yrangepos", // semanticDB compiler plugin
      "-Ywarn-dead-code",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Ywarn-macros:after",
      "-Ywarn-unused:imports",
      "-Ywarn-unused:privates",
      "-Ywarn-unused:locals",
      "-Ywarn-unused:patvars",
      "-Ywarn-unused:implicits"
    ),
    
    javacOptions ++= Seq(
      "-source",
      "1.8",
      "-target",
      "1.8"
    )
)


// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `$name;format="norm"$` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, BuildInfoPlugin, GitVersioning)
    .settings(settings)
    .settings(
      libraryDependencies ++= Seq(
        library.typesafeConfig,
        library.scalaCheck,
        library.scalaTest
      )
    )


// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {

    object Version {
      val scala          = "$scala_version$"
      val scalaCheck     = "1.15.4"
      val scalaTest      = "3.2.9"
      val typesafeConfig = "1.4.1"
    }

    val typesafeConfig       = "com.typesafe"                 % "config"                    % Version.typesafeConfig
    val scalaCheck           = "org.scalacheck"               %% "scalacheck"               % Version.scalaCheck % Test
    val scalaTest            = "org.scalatest"                %% "scalatest"                % Version.scalaTest % Test
  }


// *****************************************************************************
// Settings
// *****************************************************************************        |

lazy val settings =
  commonSettings ++
    scalafmtSettings ++
    wartRemoverSettings ++
    gitSettings ++
    headerSettings ++
    buildInfoSettings ++
    publishSettings ++
    releaseSettings ++
    scoverageSettings

lazy val commonSettings =
  Seq(
    licenses += ("Apache 2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
    mappings.in(Compile, packageBin) += baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
    //unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    //unmanagedSourceDirectories.in(Test)    := Seq(scalaSource.in(Test).value)
    //incOptions := incOptions.value.withNameHashing(true),
  )


lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile         := true
  )

lazy val buildInfoSettings = Seq(
  buildInfoKeys    := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
  buildInfoPackage := "$organization$.$package$.buildinfo",
  buildInfoOptions += BuildInfoOption.BuildTime
)

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

import de.heikoseeberger.sbtheader.License._

lazy val headerSettings = Seq(
  headerLicense := Some(ALv2("$year$", "$maintainer$"))
)

lazy val wartRemoverSettings = Seq(
  Compile / compile / wartremoverErrors := Warts.unsafe,
  //  wartremoverErrors in (Compile, compile) ++= Warts.allBut(Wart.Any, Wart.StringPlusAny),
  //  wartremoverExcluded ++= (sourceManaged ** "*.scala").value.get
)

lazy val scoverageSettings = Seq(
  coverageMinimumStmtTotal         := 80,
  coverageFailOnMinimum            := true,
  Test / compile / coverageEnabled := true
)

lazy val publishSettings = Seq(
  publishMavenStyle       := true,
  Test / publishArtifact  := false,
  publishTo := {
    val nexus = "http://127.0.0.1:48081/"
    if (isSnapshot.value) {
      Some("snapshots".at(nexus + "repository/maven-snapshots"))
    } else {
      Some("releases".at(nexus + "repository/maven-releases"))
    }
  }
)

import sbtrelease.ReleasePlugin.autoImport._
import ReleaseTransformations._

lazy val releaseSettings = Seq(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    //publishDocker,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    pushChanges
  )
)

// *****************************************************************************
// Sbt Command Alias
// *****************************************************************************

addCommandAlias("testCoverage", ";clean;coverage;test;coverageReport")
