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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.YesNoFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.CreateAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.Navigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{Page, UploadSummaryPage}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.CacheDataService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.UploadSummaryView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadFormSummaryController @Inject() (
  mcc: MessagesControllerComponents,
  identify: IdentifierAction,
  data: CacheDataService,
  formProvider: YesNoFormProvider,
  val navigator: Navigator,
  summaryView: UploadSummaryView
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport with Navigation {

  override val page: Page = UploadSummaryPage

  private val form = formProvider("upload_documents_summary.add.required")

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers map { answers =>
      answers.uploads match {
        case documents if documents.nonEmpty =>
          Ok(
            summaryView(
              answers.uploadAnotherFile.fold(form)(form.fill),
              answers.claimType,
              documents,
              backLink(answers)
            )
          )
        case _ => Redirect(controllers.makeclaim.routes.UploadFormController.onPageLoad())
      }
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers flatMap { answers =>
      form.bindFromRequest().fold(
        formWithErrors =>
          Future(BadRequest(summaryView(formWithErrors, answers.claimType, answers.uploads, backLink(answers)))),
        addAnother =>
          data.updateCreateAnswers(answers => answers.copy(uploadAnotherFile = Some(addAnother))) map {
            _ =>
              if (addAnother)
                Redirect(controllers.makeclaim.routes.UploadFormController.onPageLoad())
              else
                Redirect(navigator.nextPage(UploadSummaryPage, answers))
          }
      )
    }
  }

  def onRemove(documentReference: String): Action[AnyContent] = identify.async { implicit request =>
    data.updateCreateAnswers(removeDocument(documentReference)) map { _ =>
      Redirect(controllers.makeclaim.routes.UploadFormSummaryController.onPageLoad())
    }
  }

  def removeDocument: String => CreateAnswers => CreateAnswers = (ref: String) =>
    (userAnswers: CreateAnswers) => {
      val remainingFiles = userAnswers.uploads.filterNot(_.upscanReference == ref)
      userAnswers.copy(uploads = remainingFiles)
    }

}
