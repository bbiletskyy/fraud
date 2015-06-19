package fraud.main

import akka.actor.{Actor, ActorRef}
import com.datastax.driver.core._
import fraud.main.RandomTransaction._
import spray.http.MediaTypes.{`application/json`, `text/html`}
import spray.httpx.SprayJsonSupport.{sprayJsonMarshaller, sprayJsonUnmarshaller}
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
  def communicate(t: Transaction)

  val session = initCassandraConnection()

  def initCassandraConnection() = {
    val cluster = new Cluster.Builder().
      addContactPoints("localhost").
      withPort(9042).
      withQueryOptions(new QueryOptions().setConsistencyLevel(QueryOptions.DEFAULT_CONSISTENCY_LEVEL)).build();
    val session = cluster.connect()
    session.execute(s"USE fraud")
    session
  }

  def selectFraud() = {
    session.execute("select * from fraud_transactions").all().toList.map(x => JsonParser(x.getString("transaction")).asJsObject())
  }

  import TransactionJsonProtocol._

  val route =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>Real Time Transaction Fraud Detection REST API</h1>
                <a href="/transactions">Examples</a>
                of event json format.
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
        respondWithMediaType(`application/json`) {
          complete(selectFraud())
        }
      }
    }
}