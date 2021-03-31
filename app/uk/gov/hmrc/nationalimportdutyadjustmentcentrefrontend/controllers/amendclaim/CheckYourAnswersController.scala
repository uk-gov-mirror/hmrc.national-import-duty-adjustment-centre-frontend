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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.amendclaim

import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.{AmendAnswers, AmendClaim}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.AmendNavigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{CheckYourAnswersPage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.{CacheDataService, ClaimService}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendclaim.CheckYourAnswersView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CheckYourAnswersController @Inject() (
  mcc: MessagesControllerComponents,
  identify: IdentifierAction,
  data: CacheDataService,
  service: ClaimService,
  val navigator: AmendNavigator,
  checkYourAnswersView: CheckYourAnswersView
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport with Navigation[AmendAnswers] {

  override val page: Page = CheckYourAnswersPage

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getAmendAnswers flatMap { answers =>
      try {
        val amendClaim = AmendClaim(answers)
        data.updateAmendAnswers(answers => answers.copy(changePage = None)) map { updatedAnswers =>
          Ok(checkYourAnswersView(amendClaim, backLink(updatedAnswers)))
        }
      } catch {
        case _: MissingAnswersException =>
          Future(Redirect(navigator.firstMissingAnswer(answers)))
      }
    }
  }

  def onChange(page: String): Action[AnyContent] = identify.async { implicit request =>
    data.updateAmendAnswers(answers => answers.copy(changePage = Some(page))) map { _ =>
      Redirect(navigator.gotoPage(page))
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    data.getAmendAnswers flatMap { answers =>
      val amendClaim = AmendClaim(answers)
      service.amendClaim(request.eoriNumber, amendClaim) flatMap {
        case response if response.error.isDefined => throw new Exception(s"Error - ${response.error}")
        case response =>
          data.storeAmendResponse(response) map {
            _ => Redirect(nextPage(answers))
          }
      }
    } recover {
      case _: MissingAnswersException =>
        Redirect(routes.AmendClaimController.start())
    }

  }

}
