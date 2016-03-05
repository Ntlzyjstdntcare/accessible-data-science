package ingestion

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http

/**
  * Created by colm on 05/03/16.
  */
object Boot extends App with LazyLogging {

  logger.info(" - ------------------------------------------")
  logger.info(" -      Ingestion Service started")
  logger.info(" - ------------------------------------------")

  val config = ConfigFactory.load()

  implicit val system = ActorSystem("ingestion-actors")
  val environment = system.settings.config.getString("config.resource")

  logger.info("Environment    : " + environment)
  logger.info("Actor timeout  : " + config.getLong("actor.askTimeout"))

  val service = system.actorOf()

//  IO(Http).ask(Http.Bind())

}
