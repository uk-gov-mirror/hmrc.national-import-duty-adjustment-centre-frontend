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

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.{AmendAnswers, AmendClaimResponse}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, CreateClaimResponse}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{CacheData, JourneyId}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.CacheDataRepository

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CacheDataService @Inject() (repository: CacheDataRepository)(implicit ec: ExecutionContext) {

  private def getCacheData(implicit request: IdentifierRequest[_]): Future[CacheData] =
    repository.get(request.identifier) flatMap {
      case Some(data) => Future(data)
      case None =>
        val data = CacheData(request.identifier)
        repository.insert(data) map { _ => data }
    }

  def getCreateAnswersWithJourneyId(implicit request: IdentifierRequest[_]): Future[(CreateAnswers, JourneyId)] =
    getCacheData map (cache => (cache.getCreateAnswers, cache.journeyId))

  def getAmendAnswersWithJourneyId(implicit request: IdentifierRequest[_]): Future[(AmendAnswers, JourneyId)] =
    getCacheData map (cache => (cache.getAmendAnswers, cache.journeyId))

  def getCreateAnswers(implicit request: IdentifierRequest[_]): Future[CreateAnswers] =
    getCacheData map (_.getCreateAnswers)

  def getAmendAnswers(implicit request: IdentifierRequest[_]): Future[AmendAnswers] =
    getCacheData map (_.getAmendAnswers)

  def updateCreateAnswers(
    update: CreateAnswers => CreateAnswers
  )(implicit request: IdentifierRequest[_]): Future[CreateAnswers] =
    getCacheData flatMap { data =>
      val updatedAnswers: CreateAnswers = update(data.getCreateAnswers)
      repository.update(data.copy(createAnswers = Some(updatedAnswers))) map { _ => updatedAnswers }
    }

  def updateAmendAnswers(
    update: AmendAnswers => AmendAnswers
  )(implicit request: IdentifierRequest[_]): Future[AmendAnswers] =
    getCacheData flatMap { data =>
      val updatedAnswers: AmendAnswers = update(data.getAmendAnswers)
      repository.update(data.copy(amendAnswers = Some(updatedAnswers))) map { _ => updatedAnswers }
    }

  def storeCreateResponse(
    claimResponse: CreateClaimResponse
  )(implicit request: IdentifierRequest[_]): Future[Option[CacheData]] =
    getCacheData flatMap { data =>
      repository.update(data.copy(createAnswers = None, createClaimResponse = Some(claimResponse)))
    }

  def storeAmendResponse(
    amendClaimResponse: AmendClaimResponse
  )(implicit request: IdentifierRequest[_]): Future[Option[CacheData]] =
    getCacheData flatMap { data =>
      repository.update(data.copy(amendAnswers = None, amendClaimResponse = Some(amendClaimResponse)))
    }

}
