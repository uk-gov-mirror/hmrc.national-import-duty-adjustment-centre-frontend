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
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import org.mongodb.scala.model.{IndexModel, IndexOptions}
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.{Codecs, PlayMongoRepository}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.CacheData

import scala.concurrent.{ExecutionContext, Future}

class CacheDataRepository @Inject() (mongoComponent: MongoComponent, config: AppConfig)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[CacheData](
      collectionName = "cache-data",
      mongoComponent = mongoComponent,
      domainFormat = CacheData.formats,
      indexes = Seq(
        IndexModel(ascending("id"), IndexOptions().name("idIdx").unique(true)),
        IndexModel(
          ascending("lastUpdated"),
          IndexOptions().name("userAnswersExpiry").expireAfter(config.mongoTimeToLiveInSeconds, TimeUnit.SECONDS)
        )
      ),
      replaceIndexes = config.mongoReplaceIndexes
    ) {

  def get(id: String): Future[Option[CacheData]] =
    collection.findOneAndUpdate(filter(id), set("lastUpdated", Instant.now())).toFutureOption()

  def insert(data: CacheData): Future[Unit] =
    collection.insertOne(data).toFuture().map(_ => Unit)

  def update(data: CacheData): Future[Option[CacheData]] =
    collection.findOneAndReplace(filter(data.id), data.copy(lastUpdated = Instant.now())).toFutureOption()

  def delete(id: String): Future[Unit] = collection.deleteOne(filter(id)).toFuture().map(_ => Unit)

  private def filter(id: String) =
    equal("id", Codecs.toBson(id))

}
