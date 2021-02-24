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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services

import javax.inject.Inject
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, CreateClaimResponse}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.CacheData
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.CacheDataRepository

import scala.concurrent.{ExecutionContext, Future}

class CacheDataService @Inject() (repository: CacheDataRepository)(implicit ec: ExecutionContext) {

  private def getCacheData(implicit request: IdentifierRequest[_]): Future[CacheData] =
    repository.get(request.identifier) flatMap {
      case Some(data) => Future(data)
      case None =>
        val data = CacheData(request.identifier)
        repository.set(data) map { _ => data }
    }

  def getCreateAnswers(implicit request: IdentifierRequest[_]): Future[CreateAnswers] =
    getCacheData map (_.getCreateAnswers)

  def updateCreateAnswers(
    update: CreateAnswers => CreateAnswers
  )(implicit request: IdentifierRequest[_]): Future[CreateAnswers] =
    getCacheData flatMap { data =>
      val updatedAnswers: CreateAnswers = update(data.getCreateAnswers)
      repository.set(data.copy(createAnswers = Some(updatedAnswers))) map { _ => updatedAnswers }
    }

  def updateCreateResponse(
    claimResponse: CreateClaimResponse
  )(implicit request: IdentifierRequest[_]): Future[Option[CacheData]] =
    getCacheData flatMap { data =>
      repository.set(data.copy(createAnswers = None, createClaimResponse = Some(claimResponse)))
    }

}
