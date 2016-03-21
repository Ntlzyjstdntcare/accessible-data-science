package ingestion.backend

import akka.actor._
import akka.util.Timeout
import ingestion.IngestionRestService._
import org.json4s
import scala.concurrent.duration._
import org.json4s._
import org.json4s.native.JsonMethods._

/**
  * Created by colm on 20/03/16.
  */

object NumberTopLevelElementsActor {

  def props(): Props = Props(classOf[NumberTopLevelElementsActor])
}

class NumberTopLevelElementsActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit lazy val timeout = Timeout(15.seconds)

  implicit def intToString(i: Int) = i.toString

  import system.dispatcher

//  val mySender = sender

  def receive = {
    case fer: NumberTopLevelElementsRequest =>
      log.info("FirstEDAActor - received NumberTopLevelElementsRequest")
      val mySender = sender
      context.actorOf(RedisClientActor.props, "myRedisActor") ! RedisResultsRequest(mySender)

    case rr: RedisResults =>
      log.info("FirstEDAActor - received RedisResults")
      rr.sender ! NumberTopLevelElementsResults(getNumberOfTopLevelElements(rr.redisResults))
  }

  def getNumberOfTopLevelElements(json: String): Int = {
    //Turn the String into Json and get the number of top-level(?) elements in it. Look for scala json libraries/utils. See what I use in SparkJobSever.
    val jsonAST = parse(json)

    jsonAST.children.size
  }



}
