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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Claim
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingUserAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.{CacheDataService, CreateClaimService}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.CheckYourAnswersPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class CheckYourAnswersController @Inject() (
  mcc: MessagesControllerComponents,
  identify: IdentifierAction,
  data: CacheDataService,
  service: CreateClaimService,
  checkYourAnswersPage: CheckYourAnswersPage
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport {

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getAnswers map { answers =>
      Ok(checkYourAnswersPage(Claim(answers)))
    } recover {
      case _: MissingUserAnswersException =>
        Redirect(controllers.routes.StartController.start())
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    data.getAnswers flatMap { answers =>
      val claim = Claim(answers)
      service.submitClaim(claim) flatMap {
        case response if response.error.isDefined => throw new Exception(s"Error - ${response.error}")
        case response =>
          data.updateResponse(response) map {
            _ => Redirect(routes.ConfirmationController.onPageLoad())
          }
      }
    } recover {
      case _: MissingUserAnswersException =>
        Redirect(controllers.routes.StartController.start())
    }

  }

}
