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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.makeclaim

import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.NIDACConnector
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.{DataRequiredAction, IdentifierAction}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.CreateClaimRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingUserAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.CacheDataRepository
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.CheckYourAnswersPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersController @Inject() (
  mcc: MessagesControllerComponents,
  identify: IdentifierAction,
  requireData: DataRequiredAction,
  connector: NIDACConnector,
  repository: CacheDataRepository,
  checkYourAnswersPage: CheckYourAnswersPage
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen requireData) { implicit request =>
    request.cacheData.answers match {
      case Some(answers) => Ok(checkYourAnswersPage(CreateClaimRequest(request.internalId, answers)))
      case None          => Redirect(controllers.routes.StartController.start())
    }
  }

  def onSubmit(): Action[AnyContent] = (identify andThen requireData).async { implicit request =>
    request.cacheData.answers match {
      case Some(answers) =>
        val claim = CreateClaimRequest(request.internalId, answers)
        connector.submitClaim(claim) flatMap { response =>
          repository.set(request.cacheData.copy(answers = None, createClaimResponse = Some(response))) map {
            _ => Redirect(routes.ConfirmationController.onPageLoad())
          }
        }
      case None => Future(missingAnswersError)
    }

  }

  private def missingAnswersError = throw new MissingUserAnswersException("Missing UserAnswers")

}
