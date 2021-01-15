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
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.{DataRequest, IdentifierRequest}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.UserAnswersRepository

import scala.concurrent.{ExecutionContext, Future}

class DataRequiredActionImpl @Inject() (val sessionRepository: UserAnswersRepository)(implicit
  val executionContext: ExecutionContext
) extends DataRequiredAction {

  override protected def refine[A](request: IdentifierRequest[A]): Future[Either[Result, DataRequest[A]]] =
    sessionRepository.get(request.identifier).map {
      case None =>
        Left(Redirect(routes.StartController.start()))
      case Some(userAnswers) if userAnswers.claimReference.nonEmpty =>
        Left(Redirect(routes.StartController.start()))
      case Some(userAnswers) =>
        Right(DataRequest(request.request, request.identifier, userAnswers))
    }

}

trait DataRequiredAction extends ActionRefiner[IdentifierRequest, DataRequest]
