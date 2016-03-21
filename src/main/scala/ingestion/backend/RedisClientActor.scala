package ingestion.backend

import akka.actor.{ActorSystem, ActorLogging, Actor, Props}
import akka.util.Timeout
import ingestion.IngestionRestService.{RedisResults, RedisResultsRequest, NumberTopLevelElementsResults, APIResults}
import scala.concurrent.duration._
import com.redis._

import scala.util.Try

object RedisClientActor {

  def props(): Props = Props(classOf[RedisClientActor])
}

class RedisClientActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit lazy val timeout = Timeout(15.seconds)

  val client = new RedisClient("localhost", 6379)

  import system.dispatcher

  def receive = {
    case ar: APIResults =>
      log.info("RedisClientActor - received APIResults")
      saveToRedis(ar.results, "apiResults")

    case rrr: RedisResultsRequest =>
      log.info("RedisClientActor - received RedisResultsRequest")
      sender ! RedisResults(readFromRedis("apiResults").get.getOrElse(""), rrr.sender)

//    case fer: FirstEDAResults =>
//      log.info("RedisClientActor - received FirstEDAResults")
//      saveToRedis(fer.edaResults, "edaResults")
  }

  def saveToRedis(resultType: String, results: String): Try[Boolean] = Try {

//    val client = new RedisClient("localhost", 6379)

    client.set(resultType, results)

  }

  def readFromRedis(resultsType: String): Try[Option[String]] = Try {

//    val client = new RedisClient("localhost", 6379)

    client.get(resultsType)
  }

}
