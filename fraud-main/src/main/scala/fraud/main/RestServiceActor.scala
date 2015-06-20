package fraud.main

import akka.actor.{ Actor, ActorRef }
import com.datastax.driver.core._
import fraud.main.RandomTransaction._
import spray.http.MediaTypes.{ `application/json`, `text/html` }
import spray.httpx.SprayJsonSupport.{ sprayJsonMarshaller, sprayJsonUnmarshaller }
import spray.json.JsonParser
import spray.routing._

import scala.collection.JavaConversions._

/** Actor Service that  */
class RestServiceActor(connector: ActorRef) extends Actor with RestService {
  def actorRefFactory = context

  def receive = runRoute(route)

  def communicate(t: Transaction) = connector ! t

  override def preStart() = println(s"Starting rest-service actor at ${context.self.path}")
}

/** This trait defines the routing */
trait RestService extends HttpService {
  import TransactionJsonProtocol._
  def communicate(t: Transaction)
  val session = Cluster.builder().addContactPoint("127.0.0.1").build().connect("fraud")
  def selectFraud() = session.execute("select * from fraud_transactions").iterator().map(_.getString("transaction")).mkString("\n")
  def selectFraudHTML() = session.execute("select * from fraud_transactions").iterator().map(t => <p>{ t.getString("transaction") }</p>)

  val route =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>Transaction Fraud Detection Engine REST API</h1>
                <a href="/fraud">View Detected Fraud Transactions</a>
								<br/>
								<a href="/transactions">View Random Transaction Examples</a>
              </body>
            </html>
          }
        }
      }
    } ~ path("transaction") {
      post {
        entity(as[Transaction]) { transaction =>
          communicate(transaction)
          complete(transaction)
        }
      }
    } ~ path("transactions") {
      get {
        respondWithMediaType(`application/json`) {
          complete(randomTransactions(10))
        }
      }
    } ~ path("fraud") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>Real Time Transaction Fraud Detection REST API</h1>
                <a href="/transactions">Clean</a>
                <p>{ selectFraudHTML() }</p>
              </body>
            </html>
          }
        }
      }
    }
}