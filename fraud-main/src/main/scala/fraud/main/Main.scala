package fraud.main

/** Object running the application. */
object Main {
  def main(args: Array[String]): Unit = {
    val driverHost = "localhost"
    val driverPort = 7777
    val receiverActorName = "receiver"
    val actorSystem = Spark.init(driverHost, driverPort, receiverActorName)
    Rest.init(actorSystem, driverHost, driverPort, receiverActorName)
  }
}