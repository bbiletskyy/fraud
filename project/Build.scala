import sbt._
import Keys._
import io.gatling.sbt.GatlingPlugin

object EventsRootBuild extends Build {
    lazy val root = Project(id = "fraud-root",
                            base = file(".")).aggregate(main, client)

    lazy val main = Project(id = "fraud-main",
                           base = file("fraud-main")).
                           settings(libraryDependencies ++= Dependencies.sparySparkAkkaHadoop)


    lazy val client = Project(id = "fraud-test",
                           base = file("fraud-test")).
                           dependsOn(main).enablePlugins(GatlingPlugin).
                           settings(libraryDependencies ++= Dependencies.gatling)

 
}

