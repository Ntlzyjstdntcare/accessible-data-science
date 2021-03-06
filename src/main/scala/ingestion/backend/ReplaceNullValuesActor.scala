package ingestion.backend

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import ingestion.IngestionRestService.{RedisResults, RedisResultsRequest, ReplaceNullValuesRequest, ReplaceNullValuesResponse}
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.concurrent.duration._

/**
  * Created by colm on 02/04/16.
  */

object ReplaceNullValuesActor {

  def props(): Props = Props(classOf[ReplaceNullValuesActor])
}

class ReplaceNullValuesActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit lazy val timeout = Timeout(15 seconds)

  var replacementValue: String = ""

  def receive = {
    case rnvr: ReplaceNullValuesRequest =>
      log.info("ReplaceNullValuesActor - received ReplaceNullValuesRequest")
      replacementValue = rnvr.replacementValue
      val mySender = sender
      context.actorOf(RedisClientActor.props, "myRedisActor") ! RedisResultsRequest(mySender)

    case rr: RedisResults =>
      log.info("ReplaceNullValuesActor - received RedisResults")
      rr.sender ! ReplaceNullValuesResponse(replaceNullValues(rr.results, replacementValue))
  }

  private def replaceNullValues(results: String, replacementValue: String): String = {

    log.info(results)
    //To-Do search and replace through the AST, rather than through the raw string.
    //How to deal with the fact that the AST could have essentially an infinite depth?
    //Need a recursive function? Surely the json4s library offers support for this?
    //Or is doing it through the raw string better? What is the point of the AST?
    val resultsWithNullsReplaced = results.replace("\"\"", "\"" + replacementValue + "\"")

    val resultsAST = parse(resultsWithNullsReplaced)

    compact(render(resultsAST))
  }

}
