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
import uk.gov.hmrc.mongo.ReactiveRepository
import uk.gov.hmrc.mongo.json.ReactiveMongoFormats
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.CacheData

import scala.concurrent.{ExecutionContext, Future}

class CacheDataRepository @Inject() (mongoComponent: ReactiveMongoComponent, config: AppConfig)(implicit
  ec: ExecutionContext
) extends ReactiveRepository[CacheData, BSONObjectID](
      collectionName = "cache-data",
      mongo = mongoComponent.mongoConnector.db,
      domainFormat = CacheData.formats,
      idFormat = ReactiveMongoFormats.objectIdFormats
    ) {

  override def indexes: Seq[Index] = super.indexes ++ Seq(
    Index(
      key = Seq("lastUpdated" -> IndexType.Ascending),
      name = Some("userAnswersExpiry"),
      options = BSONDocument("expireAfterSeconds" -> config.mongoTimeToLiveInSeconds)
    )
  )

  def get(id: String): Future[Option[CacheData]] =
    super.find("id" -> id).map(_.headOption)

  def set(data: CacheData): Future[Option[CacheData]] =
    super.findAndUpdate(
      Json.obj("id" -> data.id),
      Json.toJson(data copy (lastUpdated = LocalDateTime.now)).as[JsObject],
      upsert = true
    ).map(_.value.map(_.as[CacheData]))

  def delete(id: String): Future[Unit] =
    super
      .remove("id" -> id)
      .map(_ => Unit)

}
