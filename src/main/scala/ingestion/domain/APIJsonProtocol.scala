package ingestion.domain

import spray.json.DefaultJsonProtocol

/**
  * Created by colm on 06/03/16.
  */
trait APIJsonProtocol extends DefaultJsonProtocol {

  import ingestion.IngestionRestService._

  object APIResultsJsonProtocol extends DefaultJsonProtocol {
    implicit val apiResultsFormat = jsonFormat1(APIResults)
  }


}
