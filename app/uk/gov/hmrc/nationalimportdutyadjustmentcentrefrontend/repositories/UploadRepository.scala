/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories

import java.time.Instant
import java.util.concurrent.TimeUnit

import com.mongodb.client.model.Indexes.ascending
import javax.inject.Inject
import org.mongodb.scala.model.Filters.{and, equal}
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.model.{IndexModel, IndexOptions}
import play.api.libs.json._
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.formats.MongoJavatimeFormats
import uk.gov.hmrc.mongo.play.json.{Codecs, PlayMongoRepository}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.Reference
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{JourneyId, UploadId}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.UploadDetails._
import uk.gov.hmrc.play.http.logging.Mdc

import scala.concurrent.{ExecutionContext, Future}

case class UploadDetails(
  uploadId: UploadId,
  journeyId: JourneyId,
  reference: Reference,
  status: UploadStatus,
  created: Instant = Instant.now()
)

object UploadDetails {

  implicit private val formatCreated: Format[Instant] = MongoJavatimeFormats.instantFormat

  val uploadedSuccessfullyFormat: OFormat[UploadedFile] = Json.format[UploadedFile]
  val uploadedFailedFormat: OFormat[Failed]             = Json.format[Failed]

  val read: Reads[UploadStatus] = (json: JsValue) => {
    val jsObject = json.asInstanceOf[JsObject]
    jsObject.value.get("_type") match {
      case Some(JsString("InProgress")) => JsSuccess(InProgress)
      case Some(JsString("Failed")) =>
        Json.fromJson[Failed](jsObject)(uploadedFailedFormat)
      case Some(JsString("UploadedSuccessfully")) =>
        Json.fromJson[UploadedFile](jsObject)(uploadedSuccessfullyFormat)
      case Some(value) => JsError(s"Unexpected value of _type: $value")
      case None        => JsError("Missing _type field")
    }
  }

  val write: Writes[UploadStatus] = {
    case InProgress => JsObject(Map("_type" -> JsString("InProgress")))
    case f: Failed =>
      Json.toJson(f)(uploadedFailedFormat).as[JsObject] + ("_type" -> JsString("Failed"))
    case s: UploadedFile =>
      Json.toJson(s)(uploadedSuccessfullyFormat).as[JsObject] + ("_type" -> JsString("UploadedSuccessfully"))
  }

  implicit val uploadStatusFormat: Format[UploadStatus] = Format(read, write)

  implicit val idFormat: OFormat[UploadId] = Json.format[UploadId]

  implicit val referenceFormat: OFormat[Reference] = Json.format[Reference]

  val format: Format[UploadDetails] = Json.format[UploadDetails]
}

/**
  * Note that mongo calls are wrapped in Mdc.preservingMdc
  * This is to ensure that logging context (e.g. x-request-id, x-session-id, etc) remains
  * intact in any code that executes after the asynchronous completion of the Mongo queries
  */
class UploadRepository @Inject() (mongoComponent: MongoComponent, config: AppConfig)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[UploadDetails](
      collectionName = "upload-data",
      mongoComponent = mongoComponent,
      domainFormat = UploadDetails.format,
      indexes = Seq(
        IndexModel(ascending("uploadId", "journeyId"), IndexOptions().name("uploadJourneyIdx").unique(true)),
        IndexModel(ascending("reference", "journeyId"), IndexOptions().name("referenceJourneyIdx").unique(true)),
        IndexModel(
          ascending("created"),
          IndexOptions().name("uploadExpiry").expireAfter(config.mongoTimeToLiveInSeconds, TimeUnit.SECONDS)
        )
      ),
      replaceIndexes = config.mongoReplaceIndexes
    ) {

  def add(uploadDetails: UploadDetails): Future[Boolean] = Mdc.preservingMdc {
    collection.insertOne(uploadDetails).toFutureOption().map(_ => true)
  }

  def findUploadDetails(uploadId: UploadId, journeyId: JourneyId): Future[Option[UploadDetails]] = Mdc.preservingMdc {
    collection.find(
      and(equal("uploadId", Codecs.toBson(uploadId)), equal("journeyId", Codecs.toBson(journeyId)))
    ).toFuture().map(_.headOption)
  }

  def updateStatus(reference: Reference, journeyId: JourneyId, newStatus: UploadStatus): Future[UploadStatus] =
    Mdc.preservingMdc {
      collection.findOneAndUpdate(
        and(equal("reference", Codecs.toBson(reference)), equal("journeyId", Codecs.toBson(journeyId))),
        set("status", Codecs.toBson(newStatus))
      ).toFuture() map (details => details.status)
    }

}
