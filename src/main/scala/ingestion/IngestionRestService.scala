package ingestion

import akka.actor.{Props, ActorLogging, Actor}
import akka.util.Timeout
import ingestion.backend.APIActor
import ingestion.routing.PerRequestCreator
import spray.routing._

import scala.concurrent.duration._


object IngestionRestService {
  def props(): Props = Props(classOf[IngestionRestServiceActor])

  sealed trait APIMessage

  case class APIResultsRequest() extends APIMessage
  case class APIResults(results: String) extends APIMessage
}


class IngestionRestServiceActor extends Actor with ActorLogging with HttpService with PerRequestCreator{

  import IngestionRestService._

  def actorRefFactory = context
  implicit def executionContext = actorRefFactory.dispatcher

  implicit val timeout = Timeout(5 seconds)

//  val corsAllowedHostRules = List[String => Boolean](
//    hostname => true,
//    hostname => hostname.contains("localhost")
//  )

  val APIActorDelegate: Props = {
    APIActor.props()
  }

  def receive = runRoute( myRoute )

  def myRoute =
      get {
        path("datagovuk") {
          routeMessage(APIActorDelegate, APIResultsRequest())
        }
      }

  def routeMessage(delegate: Props, message: APIMessage): Route = {
    ctx => perRequest(ctx, delegate, message)
  }


}
