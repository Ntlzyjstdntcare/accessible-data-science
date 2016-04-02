package ingestion.routing

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import ingestion.IngestionRestService._
import ingestion.domain.APIJsonProtocol
import ingestion.routing.PerRequest.{WithActorRef, WithProps}
import spray.http.StatusCode
import spray.http.StatusCodes._
import spray.httpx.SprayJsonSupport
import spray.routing.RequestContext

import scala.concurrent.duration._

/**
  * Created by colm on 05/03/16.
  */
trait PerRequest extends Actor with SprayJsonSupport with APIJsonProtocol with ActorLogging {

  import APIResultsJsonProtocol._
  import NumberTopLevelElementsResultsJsonProtocol._
  import ReplaceNullValuesResponseProtocol._
  import SaveToCassandraResponseJsonProtocol._
  import context._
  import ingestion.IngestionRestService.IngestionMessage

  def r: RequestContext
  def target: ActorRef
  def message: IngestionMessage

  setReceiveTimeout(58.seconds)
  target ! message

  def receive = {
    case ar: APIResults => complete(OK, ar)
    case fer: NumberTopLevelElementsResults => complete(OK, fer)
    case stcr: SaveToCassandraResponse => complete(OK, stcr)
    case rnvr: ReplaceNullValuesResponse => complete(OK, rnvr)
    case ReceiveTimeout => complete(GatewayTimeout, "Request timeout")
  }

  override val supervisorStrategy =
    AllForOneStrategy() {
      case e: IllegalArgumentException => {
        complete(BadRequest, e.getMessage())
        Stop
      }
      case e: Throwable => {
        complete(InternalServerError, e.getMessage)
        Stop
      }
    }

  def complete(status: StatusCode, obj: APIResults) = {
    log.info("PerRequest - received APIResults")
    r.complete(status, obj)
    stop(self)
  }

  def complete(status: StatusCode, obj: NumberTopLevelElementsResults) = {
    log.info("PerRequest - received NumberTopLevelElementsResults")
    r.complete(status, obj)
    stop(self)
  }

  def complete(status: StatusCode, obj: SaveToCassandraResponse) = {
    log.info("PerRequest - received SaveToCassandraResponse")
    r.complete(status, obj)
    stop(self)
  }

  def complete(status: StatusCode, obj: ReplaceNullValuesResponse) = {
    log.info("PerRequest - received ReplaceNullValuesResponse")
    r.complete(status, obj)
    stop(self)
  }

  def complete(status: StatusCode, obj: String) = {
    log.info("PerRequest - received String message")
    r.complete(status, obj)
    stop(self)
  }


}

object PerRequest {
  case class WithActorRef(r: RequestContext, target: ActorRef, message: IngestionMessage) extends PerRequest

  case class WithProps(r: RequestContext, props: Props, message: IngestionMessage) extends PerRequest {
    lazy val target = context.actorOf(props)
  }

}

trait PerRequestCreator {
  this: Actor =>

  def perRequest(r: RequestContext, target: ActorRef, message: IngestionMessage) =
    context.actorOf(Props(new WithActorRef(r, target, message)))

  def perRequest(r: RequestContext, props: Props, message: IngestionMessage) = {
    context.actorOf(Props(new WithProps(r, props, message)))
  }
}
