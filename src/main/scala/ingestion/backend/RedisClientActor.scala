package ingestion.backend

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import com.redis._
import ingestion.IngestionRestService.{APIResults, RedisResults, RedisResultsRequest}

import scala.concurrent.duration._
import scala.util.Try

object RedisClientActor {

  def props(): Props = Props(classOf[RedisClientActor])
}

class RedisClientActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit lazy val timeout = Timeout(15.seconds)

  val client = new RedisClient("localhost", 6379)

  def receive = {
    case ar: APIResults =>
      log.info("RedisClientActor - received APIResults")
      saveToRedis(ar.results, "apiResults")

    case rrr: RedisResultsRequest =>
      log.info("RedisClientActor - received RedisResultsRequest")
      sender ! RedisResults(readFromRedis("apiResults").get.getOrElse(""), rrr.sender)

  }

  private def saveToRedis(resultType: String, results: String): Try[Boolean] = Try {

    client.set(resultType, results)

  }

  protected def readFromRedis(resultsType: String): Try[Option[String]] = Try {

    client.get(resultsType)
  }

}
