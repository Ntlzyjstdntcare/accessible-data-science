package ingestion.backend

import akka.actor.{ActorSystem, Actor, ActorLogging, Props}
import akka.util.Timeout
import ingestion.IngestionRestService.{GroupByKeyResponse, GroupByKeyRequest}
import scala.concurrent.duration._

/**
  * Created by colm on 17/04/16.
  */

object GroupByKeyActor {
  def props(): Props = Props(classOf[GroupByKeyActor])
}

class GroupByKeyActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit lazy val timeout = Timeout(15 seconds)

  val myJson = """{
    "person": {
      "name": "Joe",
      "age": 35,
      "spouse": {
      "person": {
      "name": "Marilyn",
      "age": 33
    }
    }
    }
  }"""

  val dummyResponseJson: String = """{ "person": ["Joe", "Marilyn"] }"""

  def receive = {
    case gbkr: GroupByKeyRequest =>
      log.info("GroupByKeyActor - received GroupByKeyRequest")
      sender ! GroupByKeyResponse(dummyResponseJson)
  }

  private def groupByKey(key: String, dataset: String): String = {
    "test"
  }
}
