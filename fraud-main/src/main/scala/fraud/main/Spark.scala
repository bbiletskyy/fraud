package fraud.main

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkEnv
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.classification.NaiveBayesModel
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.receiver.ActorHelper

import com.datastax.spark.connector.toSparkContextFunctions

import akka.actor.Actor
import akka.actor.Props
import fraud.main.TransactionJsonProtocol.TransactionFormat
import spray.json.pimpAny

/** Object in charge of running real stream analytics */
object Spark {
  def init(driverHost: String, driverPort: Int, receiverActorName: String) = {
    val conf = sparkConf(driverHost, driverPort)
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc, Seconds(1))
    val model = train("fraud", "training_set", sc)

    val actorStream = ssc.actorStream[Transaction](Props[Receiver], receiverActorName)
    val fraudTransactions = actorStream.filter(t => isFraud(t, model)).map(t => (t.id, t.toJson.compactPrint))
    fraudTransactions.foreachRDD { rdd => rdd.foreach { t => println } }

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

  /** Predicts whether a transaction is a fraud. Returns <code>true</code> if transaction is fraud given the model, else returns <code>false</code>. */
  def isFraud(transaction: Transaction, model: NaiveBayesModel): Boolean = {
    model.predict(Domain.features(transaction)) == 1.0
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
