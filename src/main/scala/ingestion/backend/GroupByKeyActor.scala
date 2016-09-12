package ingestion.backend

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import ingestion.IngestionRestService.{GroupByKeyRequest, GroupByKeyResponse, RedisResults, RedisResultsRequest}
import org.json4s
import org.json4s.JsonAST.{JField, JValue}
import org.json4s._
import org.json4s.native.JsonMethods._

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

/**
  * Created by colm on 17/04/16.
  */

object GroupByKeyActor {
  def props(): Props = Props(classOf[GroupByKeyActor])
}

//To-Do: either simply cut out the json4s stuff from the return string to format it, or
//else use json4s methods to further parse the JValues further. So, if the key has a list of
//JFields attached, we want to return those JFields as key-value pairs. If the key just has a
//list of values, we return those values as a list of Strings.
//
//To-Do: See Chapter 15 - Case Classes and Pattern Matching in Programming in Scala,
//I want to find out how to use case classes and pattern matching with Json when I don't
//already know the structure of the Json. Google it.


class GroupByKeyActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit lazy val timeout = Timeout(15 seconds)

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

  private def groupByKey(key: String, dataset: json4s.JValue): List[String] = {

    var valuesList = new ListBuffer[String]()

    def iterateThroughAST(dataset: JValue/*, valuesList: List[String]*/): Unit = {
      println("In iterateThroughAST")

      for {
        JObject(child) <- dataset
        JField(string, value) <- child
      } findKey(string, value)
    }

    def findKey(string: String, value: JsonAST.JValue): Unit = {
      iterateThroughAST(value)
      if (string.equals(key)) {
        println("String equals key yayy")
        addValueToResultsList(value)
      }
    }

    def addValueToResultsList(value: JsonAST.JValue): Unit = {
      println("In addValueToResultsList")
      //Much better! Returns a List containing one map of key to String values (before I cast to String of course)
      //I can work with this
      //Do I want to get the .values of the original dataset and work with that Map rather than using the for comprehension
      //over the JValues? Probably not. Ultimately I want to get the value associated with the particular key in the JField.
      //If I'm just returning a list of strings then I have the solution already. But what if the value in the JField contains
      //nested key-value pairs. I have to handle this, possibly recursively. Probably using pattern-matching, and case classes.
      valuesList += value.values.toString
    }
    iterateThroughAST(dataset)
    valuesList.toList
  }
}
