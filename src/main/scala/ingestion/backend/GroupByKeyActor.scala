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

//  var valuesList: List[String] = Nil

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

//    println("key is: " + key)
    println("original dataset is: " + dataset.values.toString)

    var valuesList = new ListBuffer[String]()

//    dataset.withFilter(x => x.isInstanceOf[JObject]).foreach(x => println("A child of the original dataset: " + x.toString))

//    datasetAST.map(x => if (x.isInstanceOf[JField])  )

//    def iterateThroughAST(dataset: JValue/*, valuesList: List[String]*/): List[String] = {
     def iterateThroughAST(dataset: JValue/*, valuesList: List[String]*/): Unit = {
//      dataset.flatMap(x => x)
       println("In iterateThroughAST")
//      println("dataSet is: " + dataset.toString)
//       val Jobjects = dataset.withFilter(x => x.isInstanceOf[JObject])

//       Jobjects.foreach(x => println("Hopefully one of the JObjects of the original dataset: " + x.toString))

//       val Jfields = Jobjects.flatMap((x, y) => (x, y))

//       Jfields.foreach(x => println("Hopefully one of the JFields of the original dataset: " + x.toString))

//       dataset.children.flatMap(x => x.)
//      val x: List[String] = for {

       for (JObject(child) <- dataset) {
         for (JField(string, value) <- child) {
           iterateThroughAST(value)
           if (string.equals(key)) {
             println("String equals key yayy")
             addValueToResultsList(value)
           }
         }
       }

//       for {
//         JObject(child) <- dataset
//         //        dummy = println("child of JObject is: " + child.toString())
//         JField(string, value) <- child
//
//
//         //dummy2 = println("string in JField is: " + string)
//         dummy = iterateThroughAST(value /*, valuesList*/)
//         //         JString(string) <- value
//         if (string.equals(key)) //{ value.toString :: valuesList}
//         dummy2 = println("String equals key yayyy")
//         dummy3 = addValueToResultsList(value)
//       //         dummy3 = addValueToResultsList(string)
//       } yield value.toString

//      if (x.isEmpty) iterateThroughAST()
//      x
//      addValueToResultsList(value/*, valuesList*/)
    }

    def addValueToResultsList(value: JsonAST.JValue): Unit = {
//    def addValueToResultsList(value: String): Unit = {
      println("In addValueToResultsList")
//      println("Adding result of for comprehension to valuesList: " + value.toString)
      //Much better! Returns a List containing one map of key to String values (before I cast to String of course)
      //I can work with this
      //Do I want to get the .values of the original dataset and work with that Map rather than using the for comprehension
      //over the JValues? Probably not. Ultimately I want to get the value associated with the particular key in the JField.
      //If I'm just returning a list of strings then I have the solution already. But what if the value in the JField contains
      //nested key-value pairs. I have to handle this, possibly recursively.
      valuesList += value.values.toString
//      valuesList += value
//      println("value is: " + value.toString)
//      println("valuesList now: " + valuesList)
//      println("value returned by sending our value through iterateThroughAST recursively: " + iterateThroughAST(value).toString())
//      println("valuesList after adding new value to it: " + valuesList.toString())
    }
//    val y: List[String] = List.concat(valuesList, iterateThroughAST(dataset))
    iterateThroughAST(dataset)
//    println("valuesList is: " + valuesList.toString())
    valuesList.toList
//    y
//    "test"
  }
}
