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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{Claim, CreateAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.{AmendNavigator, CreateNavigator}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{CheckYourAnswersPage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.{CacheDataService, CreateClaimService}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendclaim.CheckYourAnswersView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.ExecutionContext

@Singleton
class CheckYourAnswersController @Inject() (
  mcc: MessagesControllerComponents,
  identify: IdentifierAction,
  data: CacheDataService,
  service: CreateClaimService,
  val navigator: AmendNavigator,
  checkYourAnswersView: CheckYourAnswersView
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport with Navigation[AmendAnswers] {

  override val page: Page = CheckYourAnswersPage

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getAmendAnswers map { answers =>
      Ok(checkYourAnswersView(answers, backLink(answers)))
    }
  }

  def onSubmit(): Action[AnyContent] = identify { implicit request =>
    // TODO submit amend claim request and display confirmation
    Redirect(routes.CheckYourAnswersController.onPageLoad())

  }

}