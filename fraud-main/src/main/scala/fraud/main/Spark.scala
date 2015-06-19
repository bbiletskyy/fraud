package fraud.main

import akka.actor.Actor.Receive
import akka.actor.{ Actor, ActorIdentity, Identify, Props }
import java.util.concurrent.Executors
import org.apache.spark._
import org.apache.spark.SparkContext._
import org.apache.spark.storage._
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream._
import org.apache.spark.streaming.receiver._
import scala.concurrent.Await
import org.apache.spark.streaming.StreamingContext._
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.classification.NaiveBayesModel

import spray.json._

import com.datastax.spark.connector._
import com.datastax.spark.connector.streaming._

/** Object in charge of running real stream analytics */
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

  /** Trains and returns a Naive Bayes model given Cassandra keysapce name and the table name where the training set is available.*/
  def train(keySpace: String, table: String, sc: SparkContext): NaiveBayesModel = {
    val trainingSet = sc.cassandraTable(keySpace, table)
    val lps = trainingSet.map { r => LabeledPoint(r.getDouble("class_id"), Vectors.dense(r.getDouble("destination_id"), r.getDouble("amount_id"))) }
    NaiveBayes.train(lps, lambda = 1.0)
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
