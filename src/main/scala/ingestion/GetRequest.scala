package ingestion


import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.http.HttpMethods._
import spray.http.{HttpRequest, HttpResponse, Uri}

import scala.concurrent.Future

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by colm on 07/02/16.
  */

//object MyProtocol extends DefaultJsonProtocol {
//  implicit val resultsFormat = jsonFormat1(MyResult)
//}
//
//case class MyResult (myJson: String)
//test?
class GetRequest {

  implicit val system: ActorSystem = ActorSystem()
  implicit val timeout: Timeout = Timeout(15.seconds)
  import system.dispatcher // implicit execution context

//  import MyProtocol._
//  import SprayJsonSupport._



//  val pipeline = sendReceive ~> unmarshal[MyResult]
//
//  val responseFuture = pipeline {
//    Get("https://data.gov.uk/data/api/service/health/distinct")
//  }
//
//  responseFuture onComplete {
//    case Success(MyResult(jsonString)) => println("This is the json wha: " + jsonString)
//
//    case Failure(error) => println("There has been an error: " + error)
//  }

  val response: Future[HttpResponse] =
    (IO(Http) ? HttpRequest(GET, Uri("https://data.gov.uk/data/api/service/health/distinct"))).mapTo[HttpResponse]
//
//  response.onComplete {
//    case Success(response)  => {
//      val jsonStringHopefully = response.entity.asString
//      println("this is the json: " + jsonStringHopefully)
//    }
//    case Failure(t) => println("An error has occurred: " + t.getMessage)
//  }
}
