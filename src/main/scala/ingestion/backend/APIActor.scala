package ingestion.backend



import akka.actor._
import akka.io.IO
import akka.pattern.{ask, pipe}
import akka.util.Timeout
import ingestion.IngestionRestService.{APIResults, APIResultsRequest}
import org.json4s._
import org.json4s.native.JsonMethods._
import spray.can.Http
import spray.http.HttpMethods._
import spray.http._

import scala.concurrent.Future
import scala.concurrent.duration._

object APIActor {

  def props(): Props = Props(classOf[APIActor])
}

class APIActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit val timeout = Timeout(15.seconds)

  import system.dispatcher

  val response: Future[HttpResponse] =
    (IO(Http) ? HttpRequest(GET, Uri("https://data.gov.uk/data/api/service/health/distinct"))).mapTo[HttpResponse]

  def receive = {
    case arr: APIResultsRequest =>
      val mySender = sender
      log.info("APIActor - received APIResultsRequest")
      response map ((mySender, _)) pipeTo self

    case (sender: ActorRef, myResponse: HttpResponse) =>


      val x = parse(myResponse.entity.asString)
      val z = x.map(y => JArray(List(y)))

      println(myResponse.entity.toString)

      sender ! APIResults(myResponse.entity.asString)

      context.actorOf(RedisClientActor.props, "myRedisActor") ! APIResults(myResponse.entity.asString)
  }

}
