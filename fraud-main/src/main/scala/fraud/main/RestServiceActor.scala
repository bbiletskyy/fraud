package fraud.main

import akka.actor.Actor
import spray.routing._
import spray.http.MediaTypes.{ `text/html` }
import spray.http.MediaTypes.{ `application/json` }


class InitServiceActor() extends Actor with RestService {
  def actorRefFactory = context
  def receive = runRoute(route)
  def communicate(t: Transaction) = t
  override def preStart() = println(s"Starting rest-service actor at ${context.self.path}")
}

  /** Define routing */
  trait RestService extends HttpService {
    def communicate(t: Transaction)

    val route =
      path("") {
        get {
          respondWithMediaType(`text/html`) {
            complete {
              <html>
                <body>
                  <h1>Hello Rest</h1>
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
      } ~  path("transactions") {
        get {
          respondWithMediaType(`application/json`) {
            complete (Seq(RandomTransaction(), RandomTransaction(), RandomTransaction()))
          }
        }
      }
  }

