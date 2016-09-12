package ingestion.backend

import akka.actor._
import akka.util.Timeout
import ingestion.IngestionRestService._
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.concurrent.duration._

/**
  * Created by colm on 20/03/16.
  */

object NumberTopLevelElementsActor {

  def props(): Props = Props(classOf[NumberTopLevelElementsActor])
}

class NumberTopLevelElementsActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit lazy val timeout = Timeout(15 seconds)

  implicit def intToString(i: Int) = i.toString


  def receive = {
    case fer: NumberTopLevelElementsRequest =>
      log.info("NumberTopLevelElementsActor - received NumberTopLevelElementsRequest")
      val mySender = sender
      context.actorOf(RedisClientActor.props, "myRedisActor") ! RedisResultsRequest(mySender)

    case rr: RedisResults =>
      log.info("NumberTopLevelElementsActor - received RedisResults")
      rr.sender ! NumberTopLevelElementsResults(getNumberOfTopLevelElements(rr.results))
  }

  private def getNumberOfTopLevelElements(json: String): Int = {
    val jsonAST = parse(json)

    jsonAST.children.size
  }



}
