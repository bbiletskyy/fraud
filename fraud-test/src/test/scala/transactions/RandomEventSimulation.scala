package transactions


import fraud.main.RandomTransaction
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import spray.json._
import fraud.main.TransactionJsonProtocol._
import scala.concurrent.duration._

/**
 * Created by Anton Sergienko on 6/19/15.
 */
class RandomEventSimulation extends Simulation{

  val httpConf = http.baseURL("http://localhost:8080")

  val scn = scenario("Posting Random Transactions").repeat(100,"transaction_nr") {

    exec{session=>
      var transaction = if(session("transaction_nr").as[Int]%100==0) RandomTransaction.randomFraud() else RandomTransaction()
      session.set("transaction",transaction.toJson.compactPrint)}.
      exec(http("Posting a a random event")
      .post("/transaction").body(StringBody("${transaction}")).asJSON)
      .pause(5 millis)
  }

  setUp(scn.inject(atOnceUsers(10))).protocols(httpConf)

}
