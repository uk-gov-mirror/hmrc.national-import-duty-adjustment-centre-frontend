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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.{DataRequiredAction, IdentifierAction}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.CreateClaimRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.UserAnswersRepository
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.ClaimService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.CheckYourAnswersPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class CheckYourAnswersController @Inject() (
  mcc: MessagesControllerComponents,
  identify: IdentifierAction,
  requireData: DataRequiredAction,
  claimService: ClaimService,
  userAnswersRepository: UserAnswersRepository,
  checkYourAnswersPage: CheckYourAnswersPage
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(): Action[AnyContent] = (identify andThen requireData) { implicit request =>
    Ok(checkYourAnswersPage(CreateClaimRequest(request.userAnswers)))
  }

  def onSubmit(): Action[AnyContent] = (identify andThen requireData).async { implicit request =>
    claimService.submitClaim(request.userAnswers) flatMap { response =>
      val updatedCache = request.userAnswers.copy(claimReference = response.result)
      userAnswersRepository.set(updatedCache) map {
        _ => Redirect(routes.ConfirmationController.onPageLoad())
      }
    }
  }

}
