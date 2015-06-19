package fraud.main

/** Object running the application */
object Main {
  def main(args: Array[String]): Unit = {
    println("Hello World!")
    val actorSystem = Spark.init("localhost", 7777, "receiver")
    Rest.init(actorSystem, "localhost", 7777, "receiver")
  }
}