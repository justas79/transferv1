name := """transferv1"""
organization := "com.justbatu"

version := "1.0-SNAPSHOT"


//lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)
lazy val transferv1 = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(evolutions, jdbc)
libraryDependencies += guice

libraryDependencies += "com.h2database" % "h2" % "1.4.192"
libraryDependencies += "org.hamcrest" % "hamcrest-all" % "1.3" % "test"

maintainer := "jbatulevicius@gmail.com"
