package ingestion

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import akka.util.Timeout
import ingestion.backend.{ReplaceNullValuesActor, CassandraClientActor, NumberTopLevelElementsActor, APIActor}
import ingestion.routing.PerRequestCreator
import spray.http.HttpHeaders.{`Access-Control-Allow-Headers`, `Access-Control-Allow-Origin`}
import spray.http.{SomeOrigins, HttpOrigin, HttpHeaders}
import spray.routing._

import scala.concurrent.duration._


object IngestionRestService {
  def props(): Props = Props(classOf[IngestionRestServiceActor])

  sealed trait IngestionMessage

  sealed trait APIMessage extends IngestionMessage
  sealed trait EDAMessage extends IngestionMessage
  sealed trait DatabaseMessage extends IngestionMessage
  sealed trait CleaningMessage extends IngestionMessage

  case class APIResultsRequest() extends APIMessage
  case class APIResults(results: String) extends APIMessage

  case class NumberTopLevelElementsRequest() extends EDAMessage
  case class NumberTopLevelElementsResults(results: String) extends EDAMessage

  case class RedisResultsRequest(sender: ActorRef) extends DatabaseMessage
  case class RedisResults(results: String, sender: ActorRef) extends DatabaseMessage

  case class SaveToCassandraRequest() extends DatabaseMessage
  case class SaveToCassandraResponse(response: String) extends DatabaseMessage

  case class ReplaceNullValuesRequest(replacementValue: String) extends CleaningMessage
  case class ReplaceNullValuesResponse(response: String) extends CleaningMessage
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

  val NumberTopLevelElementsActorDelegate: Props = {
    NumberTopLevelElementsActor.props()
  }

  val CassandraClientActorDelegate: Props = {
    CassandraClientActor.props()
  }

  val ReplaceNullValuesDelegate: Props = {
    ReplaceNullValuesActor.props()
  }

  def receive = runRoute( myRoute )

  def myRoute = {
    allowHosts(hostname => corsAllowedHostRules.exists(f => f(hostname))) {
      get {
        path("datagovuk") {
          routeMessage(APIActorDelegate, APIResultsRequest())
        } ~
          path("numbertoplevelelements") {
            routeMessage(NumberTopLevelElementsActorDelegate, NumberTopLevelElementsRequest())
          }
      } ~ {
        post {
          path("savetocassandra") {
            routeMessage(CassandraClientActorDelegate, SaveToCassandraRequest())
          } ~
            path("replacenullvalues") {
              parameters('replacementValue.as[String]) { (replacementValue) => {
                routeMessage(ReplaceNullValuesDelegate, ReplaceNullValuesRequest(replacementValue))
              }
            }
          }
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

  def routeMessage(delegate: Props, message: IngestionMessage): Route = {
    ctx => perRequest(ctx, delegate, message)
  }


}
