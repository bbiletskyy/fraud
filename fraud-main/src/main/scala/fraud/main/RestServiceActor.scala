package fraud.main

import akka.actor.Actor
import spray.routing._
import akka.actor.ActorRef
import spray.http.MediaTypes.{ `text/html` }
import spray.http.MediaTypes.{`application/json` }
import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller
import fraud.main.RandomTransaction._

/**Actor Service that  */
class RestServiceActor(connector: ActorRef) extends Actor with RestService {
  def actorRefFactory = context
  def receive = runRoute(route)

  def communicate(t: Transaction) = connector ! t
  override def preStart() = println(s"Starting rest-service actor at ${context.self.path}")
}

/** This trait defines the routing */
trait RestService extends HttpService {
  def communicate(t: Transaction)

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
          complete (randomTransactions(10))
          }
        }
      }
}