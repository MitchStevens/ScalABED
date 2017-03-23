name := "ABED"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.17"
