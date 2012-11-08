scalaVersion := "2.9.2"

organization := "org.mephi.c-quiz"

name := "c-quiz"

version := "1.0"

resolvers += "oss-sonatype-repo" at "https://oss.sonatype.org/content/repositories/releases/"

seq(webSettings :_*)

libraryDependencies ++= Seq(
  "net.liftweb" % "lift-webkit_2.9.2" % "2.5-M2",
  "net.debasishg" % "sjson_2.9.1" % "0.17",
  "commons-io" % "commons-io" % "2.4",
  "junit" % "junit" % "4.10" % "test",
  "org.eclipse.jetty" % "jetty-server" % "8.1.7.v20120910" % "container",
  "org.eclipse.jetty" % "jetty-webapp" % "8.1.7.v20120910" % "container",
  "org.slf4j" % "slf4j-simple" % "1.7.2" % "container"
)