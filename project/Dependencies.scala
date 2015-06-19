import sbt._

object Version {
  val akka      = "2.3.8"
  val hadoop    = "2.6.0"
  val logback   = "1.1.2"
  val mockito   = "1.10.17"
  val scala     = "2.11.4"
  val scalaTest = "2.2.3"
  val slf4j     = "1.7.6"
  val spark     = "1.4.0"
  val spray     = "1.3.1"
  val sprayJson = "1.2.6"
  val sparkCassandra = "1.4.0-M1"

  val gatling   = "2.1.4"
}

object Library {
  val akkaActor      = "com.typesafe.akka" %% "akka-actor"      % Version.akka
  val akkaTestKit    = "com.typesafe.akka" %% "akka-testkit"    % Version.akka
  val hadoopClient   = "org.apache.hadoop" %  "hadoop-client"   % Version.hadoop
  val logbackClassic = "ch.qos.logback"    %  "logback-classic" % Version.logback
  val mockitoAll     = "org.mockito"       %  "mockito-all"     % Version.mockito
  val scalaTest      = "org.scalatest"     %% "scalatest"       % Version.scalaTest
  val slf4jApi       = "org.slf4j"         %  "slf4j-api"       % Version.slf4j
  val sparkStreaming = "org.apache.spark"  %% "spark-streaming" % Version.spark
  val sparkMl        = "org.apache.spark"  %% "spark-mllib"     % Version.spark
  val sprayCan 	     = "io.spray"          %% "spray-can"       % Version.spray
  val sprayRouting   = "io.spray"          %% "spray-routing"   % Version.spray
  val sparyJson      = "io.spray"          %%  "spray-json"     % Version.sprayJson
  val sparkCassandra = "com.datastax.spark" %% "spark-cassandra-connector" % Version.sparkCassandra

  val gatlingHighcharts = "io.gatling.highcharts" % "gatling-charts-highcharts" % Version.gatling 
  val gatlingTestFramework =  "io.gatling"        % "gatling-test-framework"    % Version.gatling 
}

object Dependencies {

  import Library._

  val sparySparkAkkaHadoop = Seq(
    sparyJson,
    sprayCan,
    sprayRouting,
    sparkStreaming,
    sparkMl,
    sparkCassandra,
    akkaActor,
    akkaTestKit,
    hadoopClient,
    logbackClassic % "test",
    scalaTest      % "test",
    mockitoAll     % "test"
  )

  val gatling = Seq(
    gatlingHighcharts % "test",
    gatlingTestFramework % "test"
  )
}
