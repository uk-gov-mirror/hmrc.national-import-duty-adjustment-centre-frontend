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

import java.time.LocalDateTime

import javax.inject.Inject
import play.api.Configuration
import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.{BSONDocument, BSONObjectID}
import reactivemongo.play.json.ImplicitBSONHandlers._
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.Reference
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{JourneyId, JsonFormats, UploadId}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.UploadDetails._

import scala.concurrent.{ExecutionContext, Future}

case class UploadDetails(
  id: BSONObjectID,
  uploadId: UploadId,
  journeyId: JourneyId,
  reference: Reference,
  status: UploadStatus,
  created: LocalDateTime = LocalDateTime.now
)

object UploadDetails {

  import ReactiveMongoFormats.mongoEntity

  val uploadedSuccessfullyFormat: OFormat[UploadedFile] = Json.format[UploadedFile]
  val uploadedFailedFormat: OFormat[Failed]             = Json.format[Failed]

  implicit private val formatCreated: OFormat[LocalDateTime] = JsonFormats.formatLocalDateTime

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

  val format: Format[UploadDetails] = mongoEntity(Json.format[UploadDetails])
}

class UploadRepository @Inject() (mongoComponent: ReactiveMongoComponent, config: Configuration)(implicit
  ec: ExecutionContext
) extends ReactiveRepository[UploadDetails, BSONObjectID](
      collectionName = "upload-data",
      mongo = mongoComponent.mongoConnector.db,
      domainFormat = UploadDetails.format,
      idFormat = ReactiveMongoFormats.objectIdFormats
    ) {

  override def indexes: Seq[Index] = super.indexes ++ Seq(
    Index(
      key = Seq("created" -> IndexType.Ascending),
      name = Some("uploadExpiry"),
      options = BSONDocument("expireAfterSeconds" -> config.get[Int]("mongodb.timeToLiveInSeconds"))
    )
  )

  def add(uploadDetails: UploadDetails): Future[Boolean] = insert(uploadDetails).map(_ => true)

  def findUploadDetails(uploadId: UploadId, journeyId: JourneyId): Future[Option[UploadDetails]] =
    find("uploadId" -> Json.toJson(uploadId), "journeyId" -> Json.toJson(journeyId)).map(_.headOption)

  def updateStatus(reference: Reference, journeyId: JourneyId, newStatus: UploadStatus): Future[UploadStatus] =
    for (
      result <- findAndUpdate(
        query = JsObject(Seq("reference" -> Json.toJson(reference), "journeyId" -> Json.toJson(journeyId))),
        update = Json.obj("$set" -> Json.obj("status" -> Json.toJson(newStatus))),
        upsert = true // TODO - do we want to upsert?  Should fail if not found
      )
    )
      yield result.result[UploadDetails].map(_.status).getOrElse(
        // TODO - change return type to Option and return None if not found
        throw new Exception("Update failed, no document modified")
      )

}
