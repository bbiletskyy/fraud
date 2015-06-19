package fraud.main
import akka.actor.{ ActorSystem, Props }
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Rest {

  def init() {
    // create actor system with the name initActorSystem
    implicit val system = ActorSystem("initActorSystem")
    val service = system.actorOf(Props(new InitServiceActor()), "rest-service")

    implicit val timeout = Timeout(5.seconds)
    IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
  }
}
