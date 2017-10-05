name := "ABED"

version := "1.0"

scalaVersion := "2.12.1"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases"

//Testing packages
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.13.4" % "test"

//XML Parsing
libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "1.0.6"

//Graph for Scala
libraryDependencies += "org.scala-graph" %% "graph-core" % "1.11.5"

//Gives Haskell TypeClasses, Monoid, Functor, Applicative, Monad, etc
libraryDependencies += "org.typelevel" %% "cats" % "0.9.0"

//JavaFX extra controls
libraryDependencies += "org.controlsfx" % "controlsfx" % "8.40.14"

// ScalaFX DSL
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.102-R11"

// SAT4j SAT solver: https://mvnrepository.com/artifact/org.ow2.sat4j/org.ow2.sat4j.core
// libraryDependencies += "org.ow2.sat4j" % "org.ow2.sat4j.core" %  "2.3.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.3",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.3" % Test,
  "com.typesafe.akka" %% "akka-cluster-tools" % "2.5.6"
)
