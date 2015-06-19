package fraud.main

import org.apache.spark.streaming.receiver.ActorHelper

import akka.actor.Actor


/** Object in charge of running stream analytics */
object Spark {
  def main(args: Array[String]): Unit = {
    println("Hello Spark")
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
