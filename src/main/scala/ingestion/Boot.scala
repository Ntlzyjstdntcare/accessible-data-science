package ingestion

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http

import scala.concurrent.duration._

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
//  logger.info("Actor timeout  : " + config.getLong("actor.askTimeout"))

  val service = system.actorOf(IngestionRestService.props(), "ingestion-service")

  implicit val timeout = Timeout(20 seconds)

  implicit val executionContext = system.dispatcher

  IO(Http).ask(Http.Bind(service, interface = "0.0.0.0", port = 8080)).mapTo[Http.Event].map {
    case Http.Bound(address) => logger.info(s"Ingestion service bound to $address")
    case Http.CommandFailed(cmd) => {
      logger.error(s"Ingestion service could not bind to 0.0.0.0:8080, ${cmd.failureMessage}")
      system.shutdown()
    }
  }

}
