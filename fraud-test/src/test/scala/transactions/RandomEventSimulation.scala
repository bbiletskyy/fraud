package transactions


import fraud.main.RandomTransaction
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._
import spray.json._
import fraud.main.TransactionJsonProtocol._

/**
 * Created by Anton Sergienko on 6/19/15.
 */
class RandomEventSimulation extends Simulation{

  val httpConf = http.baseURL("http://localhost:8080")

  val transactionFeeder = Iterator.continually(Map("transaction" -> RandomTransaction().toJson.compactPrint))
  val scn = scenario("Posting Random Transactions").repeat(10) {
    feed(transactionFeeder).
      exec(http("Posting a a random event")
      .post("/transaction").body(StringBody("${transaction}")).asJSON)
      .pause(5 millis)
  }

  setUp(scn.inject(atOnceUsers(10))).protocols(httpConf)

}
