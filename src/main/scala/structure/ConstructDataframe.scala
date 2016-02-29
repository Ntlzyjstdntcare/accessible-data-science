package structure

import akka.actor.ActorSystem
import akka.util.Timeout
import ingestion.GetRequest
import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

//import org.json4s._
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
  * Created by colm on 26/02/16.
  */
class ConstructDataframe {

  implicit val system: ActorSystem = ActorSystem()
  implicit val timeout: Timeout = Timeout(15.seconds)
  import system.dispatcher

  val conf = new SparkConf().setAppName("accessible-data-science").setMaster("local[*]")
  val sc = new SparkContext(conf)
  val sqlContext = new SQLContext(sc)

  val x = new GetRequest
  val response = x.response

  response.onComplete {
      case Success(response)  => {
        val jsonString = response.entity.asString
        val jsonStringRDD = sc.parallelize(jsonString :: Nil)
//        val jsonStringRDD = jsonCharRDD.map(x => x.toString)
        val jsonStringDF = sqlContext.read.json(jsonStringRDD)
        println("this is the json: " + jsonString)
        println(jsonStringDF.head())
        println(jsonStringRDD.count())
        jsonStringDF.columns.foreach(x => println("This is a header of the dataframe" + x))
        println("Yeahhhhh BOIIIII")
      }
      case Failure(t) => println("An error has occurred: " + t.getMessage)
    }


}
