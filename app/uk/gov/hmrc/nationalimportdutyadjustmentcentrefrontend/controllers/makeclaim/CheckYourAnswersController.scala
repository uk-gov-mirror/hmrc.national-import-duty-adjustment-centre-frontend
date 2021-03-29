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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{Claim, CreateAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.{CreateNavigator, CreatePageNames}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{CheckYourAnswersPage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.{CacheDataService, ClaimService}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.{
  CheckYourAnswersView,
  CheckYourAnswersWithMissingView
}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext
import scala.util.Try

@Singleton
class CheckYourAnswersController @Inject() (
  mcc: MessagesControllerComponents,
  identify: IdentifierAction,
  data: CacheDataService,
  service: ClaimService,
  val navigator: CreateNavigator,
  checkYourAnswersView: CheckYourAnswersView,
  errorView: CheckYourAnswersWithMissingView
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport with Navigation[CreateAnswers] {

  override val page: Page = CheckYourAnswersPage

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers flatMap { answers =>
      Try(Claim(request.eoriNumber, answers)) fold (
        error => handleMissingAnswers(answers),
        claim => {
          data.updateCreateAnswers(answers => answers.copy(changePage = None)) map { updatedAnswers =>
            Ok(checkYourAnswersView(answers, backLink(updatedAnswers)))
          }
        }
      )
    }
  }

  def onChange(page: String): Action[AnyContent] = identify.async { implicit request =>
    data.updateCreateAnswers(answers => answers.copy(changePage = Some(page))) map { _ =>
      Redirect(navigator.gotoPage(page))
    }
  }

  def onResolve(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers map { answers =>
      Redirect(navigator.firstMissingAnswer(answers))
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers flatMap { answers =>
      Try(Claim(request.eoriNumber, answers)) fold (
        error => handleMissingAnswers(answers),
        claim =>
          service.submitClaim(claim) flatMap {
            case response if response.error.isDefined => throw new Exception(s"Error - ${response.error}")
            case response =>
              data.storeCreateResponse(response) map {
                _ => Redirect(nextPage(answers))
              }
          }
      )
    }
  }

  private def handleMissingAnswers(answers: CreateAnswers)(implicit request: IdentifierRequest[_]) =
    data.updateCreateAnswers(answers => answers.copy(changePage = Some(CreatePageNames.checkYourAnswers))) map {
      updatedAnswers =>
        BadRequest(errorView(answers, backLink(updatedAnswers)))
    }

}
