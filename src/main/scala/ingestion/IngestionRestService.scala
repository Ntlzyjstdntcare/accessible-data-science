package ingestion

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.util.Timeout
import ingestion.backend.{NumberTopLevelElementsActor, APIActor}
import ingestion.routing.PerRequestCreator
import spray.http.HttpHeaders.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Origin`}
import spray.http.{SomeOrigins, HttpOrigin, HttpHeaders}
import spray.routing._

import scala.concurrent.duration._


object IngestionRestService {
  def props(): Props = Props(classOf[IngestionRestServiceActor])

  sealed trait APIMessage
  sealed trait RedisMessage

  case class APIResultsRequest() extends APIMessage
  case class APIResults(results: String) extends APIMessage

  case class NumberTopLevelElementsRequest() extends APIMessage
  case class NumberTopLevelElementsResults(results: String) extends APIMessage

  case class RedisResultsRequest(sender: ActorRef) extends RedisMessage
  case class RedisResults(redisResults: String, sender: ActorRef) extends RedisMessage
}


class IngestionRestServiceActor extends Actor with ActorLogging with HttpService with PerRequestCreator{

  import IngestionRestService._

  def actorRefFactory = context
  implicit def executionContext = actorRefFactory.dispatcher

  implicit val timeout = Timeout(20 seconds)

  val corsAllowedHostRules = List[String => Boolean](
    hostname => true,
    hostname => hostname.contains("localhost")
  )

  val APIActorDelegate: Props = {
    APIActor.props()
  }

  val FirstEDAActorDelegate: Props = {
    NumberTopLevelElementsActor.props()
  }

  def receive = runRoute( myRoute )

  def myRoute = {
    allowHosts(hostname => corsAllowedHostRules.exists(f => f(hostname))) {
      get {
        path("datagovuk") {
          routeMessage(APIActorDelegate, APIResultsRequest())
        } ~
          path("firsteda") {
            routeMessage(FirstEDAActorDelegate, NumberTopLevelElementsRequest())
          }
      }
    }
  }

  def allowHosts(f: String => Boolean): Directive0 = mapInnerRoute { innerRoute =>
    optionalHeaderValueByType[HttpHeaders.Origin]() { originOption =>
      originOption flatMap {
        case HttpHeaders.Origin(list) => list.find {
          case HttpOrigin(_, HttpHeaders.Host(hostname, _)) => {
            val allowed = f(hostname)
            allowed
          }
        }
      } map { goodOrigin =>
        respondWithHeaders(
            `Access-Control-Allow-Headers`(Seq("Origin", "X-Requested-With", "Content-Type", "Accept")),
            `Access-Control-Allow-Origin`(SomeOrigins(Seq(goodOrigin)))
        ) {
          options {
            complete {
              ""
            }
          } ~ {
            innerRoute
          }
        }
      } getOrElse {
        innerRoute
      }
    }
  }

  def routeMessage(delegate: Props, message: APIMessage): Route = {
    ctx => perRequest(ctx, delegate, message)
  }


}
