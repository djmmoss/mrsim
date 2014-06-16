version := "0.1"

scalaVersion := "2.10.3"

addSbtPlugin("com.github.scct" % "sbt-scct" % "0.2.1")

libraryDependencies += "edu.berkeley.cs" %% "chisel" % "2.2.7"

resolvers ++= Seq(Resolver.sonatypeRepo("releases"),
                  Resolver.sonatypeRepo("snaspshots"))

