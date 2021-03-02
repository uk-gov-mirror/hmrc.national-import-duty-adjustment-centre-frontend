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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, CreateClaimResponse, SubmittedClaim}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{CacheData, JourneyId}
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

  def getCreateAnswersWithJourneyId(implicit request: IdentifierRequest[_]): Future[(CreateAnswers, JourneyId)] =
    getCacheData map (cache => (cache.getCreateAnswers, cache.journeyId))

  def getAmendAnswersWithJourneyId(implicit request: IdentifierRequest[_]): Future[(AmendAnswers, JourneyId)] =
    getCacheData map (cache => (cache.getAmendAnswers, cache.journeyId))

  def getCreateAnswers(implicit request: IdentifierRequest[_]): Future[CreateAnswers] =
    getCacheData map (_.getCreateAnswers)

  def getSubmittedClaim(implicit request: IdentifierRequest[_]): Future[SubmittedClaim] =
    getCacheData map (_.submitedClaim.getOrElse(throw new MissingAnswersException("No submitted claim")))

  def getAmendAnswers(implicit request: IdentifierRequest[_]): Future[AmendAnswers] =
    getCacheData map (_.getAmendAnswers)

  def updateCreateAnswers(
    update: CreateAnswers => CreateAnswers
  )(implicit request: IdentifierRequest[_]): Future[CreateAnswers] =
    getCacheData flatMap { data =>
      val updatedAnswers: CreateAnswers = update(data.getCreateAnswers)
      repository.set(data.copy(createAnswers = Some(updatedAnswers))) map { _ => updatedAnswers }
    }

  def updateAmendAnswers(
    update: AmendAnswers => AmendAnswers
  )(implicit request: IdentifierRequest[_]): Future[AmendAnswers] =
    getCacheData flatMap { data =>
      val updatedAnswers: AmendAnswers = update(data.getAmendAnswers)
      repository.set(data.copy(amendAnswers = Some(updatedAnswers))) map { _ => updatedAnswers }
    }

  def storeCreateResponse(
    claimResponse: CreateClaimResponse
  )(implicit request: IdentifierRequest[_]): Future[Option[CacheData]] =
    getCacheData flatMap { data =>
      repository.set(
        data.copy(
          createAnswers = None,
          createClaimResponse = Some(claimResponse),
          submitedClaim =
            claimResponse.result.map(result => SubmittedClaim(result.caseReference, data.createAnswers.get))
        )
      )
    }

}
