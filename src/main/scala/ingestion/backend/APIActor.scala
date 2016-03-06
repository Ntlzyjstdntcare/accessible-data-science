package ingestion.backend



import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import ingestion.IngestionRestService.{APIResults, APIResultsRequest}
import org.apache.commons.httpclient._
import org.apache.commons.httpclient.methods.GetMethod

import scala.concurrent.duration._

object APIActor {

  def props(): Props = Props(classOf[APIActor])
}

class APIActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit val timeout = Timeout(15.seconds)

//  val response: Future[HttpResponse] =
//    (IO(Http) ? HttpRequest(GET, Uri("https://data.gov.uk/data/api/service/health/distinct"))).mapTo[HttpResponse]

  val endpoint = "https://data.gov.uk/data/api/service/health/distinct"

  val getMethod = new GetMethod(endpoint)

  val client = new HttpClient

  val response = client.executeMethod(getMethod)

  val x = getMethod.getResponseBodyAsString

  def receive = {

    case APIResultsRequest() => {
      log.info("APIActor - received APIResultsRequest")
      sender ! APIResults(x)
    }
  }

//  def receive = {
//    case arr: APIResultsRequest => {
//      response onComplete {
//        case Success(response) => sender ! response
//        case Failure(e) => sender ! APIResults(e.getMessage)
//      }
//    }
//  }

//  def receive = {
//    case arr: APIResultsRequest => {
//      log.info("APIActor - received APIResultsRequest")
//
//    }
//  }

//  response.onComplete {
//    case Success(response) => {
//      def receive = LoggingReceive {
//
//        case arr: APIResultsRequest => {
//          log.info("APIActor - received APIResultsRequest")
//          sender ! APIResults(response.entity.asString)
//        }
//      }
//    }
//    case Failure(e) => {
//      def receive = LoggingReceive {
//
//        case arr: APIResultsRequest => {
//          log.info("APIActor - received APIResultsRequest")
//          sender ! APIResults(e.getMessage)
//        }
//      }
//
//    }
//  }
//
//  response onComplete {
//    case Success(response) => log.info("Great success"); myMethod(response.entity.asString)
//    case Failure(e) => log.info("Failure");  myMethod(e.getMessage)
//  }
//
//  def myMethod(response: String): Unit = {
//    sender ! APIResults(response)
//  }
//
//
//  def receive = LoggingReceive {
//    case arr: APIResultsRequest => {
//      log.info("APIActor - received APIResultsRequest")
//
//
//    }
//  }
}
