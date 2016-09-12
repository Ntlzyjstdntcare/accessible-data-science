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

  object ReplaceNullValuesResponseProtocol extends DefaultJsonProtocol {
    implicit val replaceNullValuesResponseFormat = jsonFormat1(ReplaceNullValuesResponse)
  }

  object GroupByKeyResponseProtocol extends DefaultJsonProtocol {
    implicit val groupByKeyResponseFormat = jsonFormat1(GroupByKeyResponse)
  }

}
