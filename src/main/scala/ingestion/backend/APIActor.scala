package ingestion.backend



import akka.actor._
import akka.io.IO
import akka.util.Timeout
import ingestion.IngestionRestService.{APIJsonResultsRequest, APIResults, APIResultsRequest}
//import org.apache.commons.httpclient._
//import org.apache.commons.httpclient.methods.GetMethod

import akka.pattern.{ask, pipe}
import spray.can.Http
import spray.http.HttpMethods._
import spray.http._

import scala.concurrent.Future
import scala.concurrent.duration._

//import scala.util.{Success, Failure}

object APIActor {

  def props(): Props = Props(classOf[APIActor])
}

class APIActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit val timeout = Timeout(15.seconds)

  import system.dispatcher

  val response: Future[HttpResponse] =
    (IO(Http) ? HttpRequest(GET, Uri("https://data.gov.uk/data/api/service/health/distinct"))).mapTo[HttpResponse]
//
//  val endpoint = "https://data.gov.uk/data/api/service/health/distinct"
//
//  val getMethod = new GetMethod(endpoint)
//
//  val client = new HttpClient
//
//  val response = client.executeMethod(getMethod)
//
//  val x = getMethod.getResponseBodyAsString

//  def receive = {
//
//    case APIResultsRequest() => {
//      log.info("APIActor - received APIResultsRequest")
//      sender ! APIResults(x)
////      sender ! APIResults("{\"county\": \"Dublin\", \"city\": \"Dublin\"}, {\"county\": \"Tipp\", \"city\": \"Cahir\"}")
//    }
//  }


//  def receive = {
//    case arr: APIResultsRequest =>
//      val mySender = sender()
//      log.info("APIActor - received APIResultsRequest")
//      APIResults(response) pipeTo mySender
//  }

  def receive = {
    case arr: APIResultsRequest =>
      val mySender = sender
      log.info("APIActor - received APIResultsRequest")
      response map((mySender, _)) pipeTo self

//    case apjrr: APIJsonResultsRequest =>


    case (sender: ActorRef, myResponse: HttpResponse) =>
      sender ! APIResults(myResponse.entity.asString)

      context.actorOf(RedisClientActor.props, "myRedisActor") ! APIResults(myResponse.entity.asString)
      //      response onComplete {
//        case Success(response) => val newSender = sender(); newSender ! APIResults(response.entity.asString)
   //     case Failure(e) => val newSender = sender(); newSender ! APIResults(e.getMessage)
     // }
  }

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
