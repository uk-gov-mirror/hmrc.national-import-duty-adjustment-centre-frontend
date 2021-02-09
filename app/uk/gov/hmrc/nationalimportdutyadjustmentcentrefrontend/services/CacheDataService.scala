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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{CacheData, CreateClaimResponse, UserAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.CacheDataRepository

import scala.concurrent.{ExecutionContext, Future}

class CacheDataService @Inject() (repository: CacheDataRepository)(implicit ec: ExecutionContext) {

  private def getCacheData(id :String): Future[CacheData] =
    repository.get(id) flatMap {
      case Some(data) => Future(data)
      case None =>
        val data = CacheData(id)
        repository.set(data) map { _ => data }
    }

  def getAnswers(id: String): Future[UserAnswers] = getCacheData(id).map(_.answers)

  def getAnswers(implicit request: IdentifierRequest[_]): Future[UserAnswers] =
    updateAnswers(answers => answers)

  def updateAnswers(update: UserAnswers => UserAnswers)(implicit request: IdentifierRequest[_]): Future[UserAnswers] =
    getCacheData(request.identifier) flatMap { data =>
      val updatedAnswers: UserAnswers = update(data.answers)
      repository.set(data.copy(answers = updatedAnswers)) map { _ => updatedAnswers }
    }

  def updateResponse(
    claimResponse: CreateClaimResponse
  )(implicit request: IdentifierRequest[_]): Future[Option[CacheData]] =
    getCacheData(request.identifier) flatMap { data =>
      repository.set(data.copy(answers = UserAnswers(), createClaimResponse = Some(claimResponse)))
    }

}
