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

  object NumberTopLevelElementsResultsJsonProtocol extends DefaultJsonProtocol {
    implicit val numberTopLevelElementsResultsFormat = jsonFormat1(NumberTopLevelElementsResults)
  }

  object SaveToCassandraResponseJsonProtocol extends DefaultJsonProtocol {
    implicit val saveToCassandraResponseFormat = jsonFormat1(SaveToCassandraResponse)
  }

}
