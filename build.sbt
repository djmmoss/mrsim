import AssemblyKeys._

assemblySettings

version := "0.1"

scalaVersion := "2.10.3"

addSbtPlugin("com.github.scct" % "sbt-scct" % "0.2.1")

libraryDependencies += "edu.berkeley.cs" %% "chisel" % "2.2.7"

libraryDependencies += "com.nicta" %% "scoobi" % "0.8.0"

resolvers ++= Seq(Resolver.sonatypeRepo("releases"),
                  Resolver.sonatypeRepo("snaspshots"))

mergeStrategy in assembly <<= (mergeStrategy in assembly) { mergeStrategy => {
 case entry => {
   val strategy = mergeStrategy(entry)
   if (strategy == MergeStrategy.deduplicate) MergeStrategy.first
   else strategy
 }
}}
