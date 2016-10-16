package ingestion

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{Matchers, BeforeAndAfterAll, FlatSpecLike}

/**
  * Created by colm on 17/09/16.
  */
class CassandraClientActorSpec (_system: ActorSystem) extends TestKit(_system)
  with ImplicitSender with FlatSpecLike with Matchers with BeforeAndAfterAll {

  def this() = this(ActorSystem("CassandraClientActor"))

  override def afterAll: Unit = {
    TestKit shutdownActorSystem(system)
  }

  "When CassandraClientActor receives a SaveToCassandraRequest it" must "send a RedisResultsRequest" in {

  }

  "When CassandraClientActor receives a RedisResults it" must "send a SaveToCassandraResponse" in {

  }

  "When getRedisResults is called it" must "send a RedisResultsRequest" in {

  }

  "When saveToCassandra is called and the call to C* succeeds it" must "return a successful SaveToCassandraResponse" in {

  }

  "When saveToCassandra is called and the call to C* fails it" must "return a failed SaveToCassandraResponse" in {

  }



}
