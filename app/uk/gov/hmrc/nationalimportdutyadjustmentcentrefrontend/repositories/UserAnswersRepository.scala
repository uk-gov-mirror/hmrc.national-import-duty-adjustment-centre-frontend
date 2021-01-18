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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UserAnswers

import scala.concurrent.{ExecutionContext, Future}

class UserAnswersRepository @Inject() (mongoComponent: ReactiveMongoComponent, config: Configuration)(implicit
  ec: ExecutionContext
) extends ReactiveRepository[UserAnswers, BSONObjectID](
      collectionName = "user-answers-cache",
      mongo = mongoComponent.mongoConnector.db,
      domainFormat = UserAnswers.formats,
      idFormat = ReactiveMongoFormats.objectIdFormats
    ) {

  override def indexes: Seq[Index] = super.indexes ++ Seq(
    Index(
      key = Seq("lastUpdated" -> IndexType.Ascending),
      name = Some("userAnswersExpiry"),
      options = BSONDocument("expireAfterSeconds" -> config.get[Int]("mongodb.timeToLiveInSeconds"))
    )
  )

  def get(id: String): Future[Option[UserAnswers]] =
    super.find("id" -> id).map(_.headOption)

  def set(userAnswers: UserAnswers): Future[Option[UserAnswers]] =
    super.findAndUpdate(
      Json.obj("id" -> userAnswers.id),
      Json.toJson(userAnswers copy (lastUpdated = LocalDateTime.now)).as[JsObject],
      upsert = true
    ).map(_.value.map(_.as[UserAnswers]))

}
