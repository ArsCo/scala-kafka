val ArsCo = "ru.ars-co"
val ProjectName = "scala-kafka"

val `Scala 2.11 version` = "2.11.8"
val `Scala 2.12 version` = "2.12.4"

lazy val commonSettings = Seq(
  organization := ArsCo,
  version := "0.0.1-SNAPSHOT",
  name := ProjectName
)

lazy val versionSettings = Seq(
  scalaVersion := `Scala 2.11 version`,
  crossScalaVersions := Seq(
    `Scala 2.11 version`,
    `Scala 2.12 version`
  )
)

lazy val nexusPublishSettings = Seq(
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomIncludeRepository := { _ => false },

  publishMavenStyle := true,
  publishArtifact in Test := false
)

lazy val scmSettings = Seq(
  scmInfo := Some(
    ScmInfo(
      url(s"https://github.com/ArsCo/$ProjectName"),
      s"scm:git@github.com:ArsCo/$ProjectName.git"
    )
  )
)

lazy val developerSettings = Seq(
  developers := List(
    Developer(
      id    = "ars",
      name  = "Arsen Ibragimov",
      email = "ars@ars-co.ru",
      url   = url("https://github.com/ars-java")
    )
  )
)

lazy val projectUrls = Seq(
  homepage := Some(url(s"https://github.com/ArsCo/$ProjectName")),
  licenses := Seq("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.html"))
)

lazy val coverageSettings = {
  import org.scoverage.coveralls.Imports.CoverallsKeys._
  Seq(
    coverallsTokenFile := sys.env.get("COVERALLS_TOKEN_DIRS").map(dir => s"$dir/$ProjectName.token")
  )
}



val Log4jVersion = "2.7"

lazy val `scala-kafka` = (project in file("."))
  .settings(
    commonSettings,
    versionSettings,

    nexusPublishSettings,
    scmSettings,
    developerSettings,

    projectUrls,
    coverageSettings,

    resolvers += "ossrh" at "https://oss.sonatype.org/content/repositories/snapshots/",

    libraryDependencies ++= /*Dependencies.logging*/ Dependencies.testing ++
                            Dependencies.kafka ++ Dependencies.zookeeper ++ Dependencies.preconditions ++
      Seq("ru.ars-co" %% "scala-common" % "0.0.1-SNAPSHOT") ++
    Seq(
      "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
      "org.slf4j" % "jcl-over-slf4j" % "1.7.21",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % Log4jVersion % Test,
      "org.apache.logging.log4j" % "log4j-api" % Log4jVersion,
      "org.apache.logging.log4j" % "log4j-core" % Log4jVersion
    )
  )

