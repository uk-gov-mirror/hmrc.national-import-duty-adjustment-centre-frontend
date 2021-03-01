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

import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.YesNoFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.{AmendNavigator}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{AttachMoreDocumentsPage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.CacheDataService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendclaim.AttachMoreDocumentsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AttachMoreDocumentsController @Inject() (
  identify: IdentifierAction,
  data: CacheDataService,
  formProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  val navigator: AmendNavigator,
  attachMoreDocumentsView: AttachMoreDocumentsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Navigation[AmendAnswers] {

  override val page: Page = AttachMoreDocumentsPage

  private val form = formProvider("amend.attach_more_documents.required")

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getAmendAnswers map { answers =>
      val preparedForm = answers.hasMoreDocuments.fold(form)(form.fill)
      Ok(attachMoreDocumentsView(preparedForm, backLink(answers)))
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors =>
        data.getAmendAnswers map { answers =>
          BadRequest(attachMoreDocumentsView(formWithErrors, backLink(answers)))
        },
      value =>
        data.updateAmendAnswers(answers => answers.copy(hasMoreDocuments = Some(value))) map {
          updatedAnswers => Redirect(nextPage(updatedAnswers))
        }
    )
  }

}
