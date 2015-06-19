package fraud.main

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkEnv
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.receiver.ActorHelper

import akka.actor.Actor
import akka.actor.Props


/** Object in charge of running stream analytics */
object Spark {
  def init(driverHost: String, driverPort: Int, receiverActorName: String) = {
    val conf = sparkConf(driverHost, driverPort)
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(1))
    
    val actorStream = ssc.actorStream[Transaction](Props[Receiver], receiverActorName)
    actorStream.foreachRDD { rdd => rdd.foreach { t => println } }
    
    
    ssc.start()
    ssc.awaitTermination(10000)
    SparkEnv.get.actorSystem
  }

  /** Returns Spark configuration */
  def sparkConf(driverHost: String, driverPort: Int) = new SparkConf(false)
    .setMaster("local[*]")
    .setAppName("Spark Streaming with Scala and Akka")
    .set("spark.logConf", "true")
    .set("spark.driver.port", driverPort.toString)
    .set("spark.driver.host", driverHost)
    .set("spark.akka.logLifecycleEvents", "true")
    .set("spark.cassandra.connection.host", "127.0.0.1")

  def main(args: Array[String]): Unit = {
    println("Starting Spark...")
    val driverHost = "localhost"
    val driverPort = 7777
    val receiverActorName = "receiver"
    Spark.init(driverHost, driverPort, receiverActorName)
  }
}

/** This actor is a bridge to Spark. It receives transactions and puts them to the spark stream */
class Receiver extends Actor with ActorHelper {
  override def preStart() = {
    println(s"Starting Spark Transaction Receiver actor at ${context.self.path}")
  }
  def receive = {
    case t: Transaction =>
      store(t)
  }
}
