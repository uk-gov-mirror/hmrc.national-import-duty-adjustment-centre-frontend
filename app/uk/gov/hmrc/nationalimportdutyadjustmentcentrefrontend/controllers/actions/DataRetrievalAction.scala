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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions

import javax.inject.Inject
import play.api.mvc.ActionTransformer
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UserAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.{DataRequest, IdentifierRequest}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.UserAnswersRepository

import scala.concurrent.{ExecutionContext, Future}

class DataRetrievalActionImpl @Inject() (val sessionRepository: UserAnswersRepository)(implicit
  val executionContext: ExecutionContext
) extends DataRetrievalAction {

  override protected def transform[A](request: IdentifierRequest[A]): Future[DataRequest[A]] = {

    def newAnswers: DataRequest[A] = DataRequest(request.request, request.identifier, UserAnswers(request.identifier))
    def existingAnswers(userAnswers: UserAnswers): DataRequest[A] =
      DataRequest(request.request, request.identifier, userAnswers)

    sessionRepository.get(request.identifier).map { answers =>
      answers.filter(_.claimReference.isEmpty).fold(newAnswers)(existingAnswers)
    }
  }

}

trait DataRetrievalAction extends ActionTransformer[IdentifierRequest, DataRequest]
