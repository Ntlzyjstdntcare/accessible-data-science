package ingestion

import scala.util.{Success, Failure}
import scala.concurrent.Future
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.util.Timeout
import akka.pattern.ask
import akka.io.IO

import spray.can.Http
import spray.http._
import HttpMethods._

/**
  * Created by colm on 07/02/16.
  */
//test?
class GetRequest {

  implicit val system: ActorSystem = ActorSystem()
  implicit val timeout: Timeout = Timeout(15.seconds)
  import system.dispatcher // implicit execution context


  val response: Future[HttpResponse] =
    (IO(Http) ? HttpRequest(GET, Uri("https://data.gov.uk/data/api/service/health/distinct"))).mapTo[HttpResponse]

  response.onComplete {
    case Success(response)  => println("this is the json" + response.toString)
    case Failure(t) => println("An error has occurred: " + t.getMessage)
  }
}
