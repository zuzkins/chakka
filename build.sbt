name := "Chakka"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.0-RC2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

seq(webSettings :_*)

libraryDependencies ++= Seq(
    "com.typesafe.akka"     %     "akka-actor_2.10.0-RC2"       %     "2.1.0-RC2",
    "com.typesafe.akka"     %     "akka-slf4j_2.10.0-RC2"       %     "2.1.0-RC2",
//    "ch.qos.logback"        %     "logback-classic"             %     "1.0.7",
    "org.slf4j"             %     "slf4j-api"                   %     "1.7.2",
    "org.slf4j"             %     "slf4j-log4j12"               %     "1.7.2",
    "com.google.code.gson"  %     "gson"                        %     "2.2.2",
    "com.typesafe.akka"     %     "akka-testkit_2.10.0-RC2"     %     "2.1.0-RC2"     % "test",
    "org.specs2"            %%    "specs2"                      %     "1.12.3"        % "test",
    "org.mockito"           %     "mockito-all"                 %     "1.9.5"         % "test"
)

libraryDependencies ++= Seq(
  "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "provided,container" artifacts (Artifact("javax.servlet", "jar", "jar"))
)

libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-webapp" % "8.1.7.v20120910" % "provided,container" artifacts (Artifact("jetty-webapp", "jar", "jar")),
  "org.eclipse.jetty" % "jetty-websocket" % "8.1.7.v20120910" % "provided,container" artifacts (Artifact("jetty-websocket", "jar", "jar"))
)
