package ingestion.backend

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.util.Timeout
import com.datastax.driver.core._
import ingestion.IngestionRestService.{RedisResults, RedisResultsRequest, SaveToCassandraRequest, SaveToCassandraResponse}

import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

/**
  * Created by colm on 25/03/16.
  */

object CassandraClientActor {
  def props(): Props = Props(classOf[CassandraClientActor])
}

class CassandraClientActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()
  implicit lazy val timeout = Timeout(15 seconds)

  def receive = {
    case stcr: SaveToCassandraRequest =>
      log.info("CassandraClientActor - received SaveToCassandraRequest")
      getRedisResults

    case rr: RedisResults =>
      rr.sender ! saveToCassandra(rr.results)
  }

  private def getRedisResults(): Unit = {
    log.info("CassandraClientActor - getting results from Redis")
    val mySender = sender
    context.actorOf(RedisClientActor.props, "myRedisActor") ! RedisResultsRequest(mySender)
  }

  private def saveToCassandra(redisResults: String): SaveToCassandraResponse = {
    log.info("CassandraClientActor - saving to Cassandra")
    val cluster = Cluster.builder().addContactPoint("localhost").build
    val session = cluster.connect("datasets")
    val boundStatement = prepareCassandraStatement(session, redisResults)
    val result = Try(session.execute(boundStatement))
    result match {
      case Success(x) => SaveToCassandraResponse("Saved to Cassandra Successfully")
      case Failure(y) => SaveToCassandraResponse("Not saved to Cassandra Successfully")
    }
  }

  private def prepareCassandraStatement(session: Session, dataset: String): BoundStatement = {
    val myUuid = java.util.UUID.randomUUID()
    val insertStatement =
      "Insert into datasets.dataset (id, dataset) values (?, ?) if not exists;"

    session.prepare(insertStatement)
             .bind(myUuid, dataset)
  }
}
