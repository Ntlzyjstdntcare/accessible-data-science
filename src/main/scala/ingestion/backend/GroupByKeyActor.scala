package ingestion.backend

import akka.actor.{ActorSystem, Actor, ActorLogging, Props}
import akka.util.Timeout
import ingestion.IngestionRestService.{RedisResultsRequest, RedisResults, GroupByKeyResponse, GroupByKeyRequest}
import org.json4s

import org.json4s
import org.json4s.JsonAST.{JValue, JField}
import scala.concurrent.duration._

import org.json4s._
import org.json4s.native.JsonMethods._

/**
  * Created by colm on 17/04/16.
  */

object GroupByKeyActor {
  def props(): Props = Props(classOf[GroupByKeyActor])
}

class GroupByKeyActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit lazy val timeout = Timeout(15 seconds)

//  val myJson = """{
//    "person": {
//      "name": "Joe",
//      "age": 35,
//      "spouse": {
//      "person": {
//      "name": "Marilyn",
//      "age": 33
//    }
//    }
//    }
//  }"""
//
//  val dummyResponseJson: String = """{ "person": ["Joe", "Marilyn"] }"""

  var groupingKey: String = "";

  def receive = {
    case gbkr: GroupByKeyRequest =>
      log.info("GroupByKeyActor - received GroupByKeyRequest")
      groupingKey = gbkr.keyToGroupBy
      val mySender = sender
      context.actorOf(RedisClientActor.props, "myRedisActorForGrouping") ! RedisResultsRequest(mySender)

    case rr: RedisResults =>
      log.info("GroupByKeyActor - received RedisResults")
      rr.sender ! GroupByKeyResponse(groupByKey(groupingKey, parse(rr.results)))
  }

//  val datasetAST = parse(dataset)



  private def groupByKey(key: String, dataset: json4s.JValue): List[String] = {
    //parse dataset into AST
    //for each JField, if the String is our key, add the JValue to our map value collection
    //Then pass the JValue into this method recursively, looking for JFields within it

    val valuesList: List[String] = Nil

//    datasetAST.map(x => if (x.isInstanceOf[JField])  )

    def iterateThroughAST(dataset: JValue/*, valuesList: List[String]*/): List[String] = {
//      dataset.flatMap(x => x)

      for {
        JObject(child) <- dataset
        JField(string, value) <- child
        addValueToResultsList(value/*, valuesList*/)
        if (string.equals(key)) //{ value.toString :: valuesList}
      } yield value.toString
    }

    def addValueToResultsList(value: JsonAST.JValue): Unit = {
      valuesList :: iterateThroughAST(value)
    }

    iterateThroughAST(dataset)
    valuesList
//    "test"
  }
}
