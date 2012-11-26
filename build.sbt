name := "Chakka"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.9.2"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

seq(webSettings :_*)

libraryDependencies ++= Seq(
    "com.typesafe.akka"     %     "akka-actor"     %     "2.0.3"
)    

libraryDependencies ++= Seq(
  "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "provided,container" artifacts (Artifact("javax.servlet", "jar", "jar"))
)

libraryDependencies ++= Seq(
  "org.eclipse.jetty" % "jetty-webapp" % "8.1.7.v20120910" % "provided,container" artifacts (Artifact("jetty-webapp", "jar", "jar")),
  "org.eclipse.jetty" % "jetty-websocket" % "8.1.7.v20120910" % "provided,container" artifacts (Artifact("jetty-websocket", "jar", "jar"))
)
