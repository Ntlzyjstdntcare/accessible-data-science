package ingestion.backend


import akka.actor.{Props, ActorSystem, ActorLogging, Actor}
import akka.event.LoggingReceive
import ingestion.IngestionRestService.{APIResults, APIResultsRequest}


object APIActor {

  def props(): Props = Props(classOf[APIActor])
}

class APIActor extends Actor with ActorLogging {

  implicit lazy val system = ActorSystem()

  def receive = LoggingReceive {
    case arr: APIResultsRequest => {
      sender ! APIResults("These are dummy results. Now implement an API query!")
    }
  }
}
