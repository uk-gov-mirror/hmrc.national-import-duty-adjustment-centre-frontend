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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.UpscanInitiateConnector
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.makeclaim.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.{FileUploading, Navigation}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UploadId
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.{
  Failed,
  UploadedFile,
  UpscanInitiateResponse
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.AmendNavigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{Page, UploadPage}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.{CacheDataService, UploadProgressTracker}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendclaim.{UploadFormView, UploadProgressView}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadFormController @Inject() (
  mcc: MessagesControllerComponents,
  identify: IdentifierAction,
  val uploadProgressTracker: UploadProgressTracker,
  val upscanInitiateConnector: UpscanInitiateConnector,
  data: CacheDataService,
  val appConfig: AppConfig,
  val navigator: AmendNavigator,
  uploadFormView: UploadFormView,
  uploadProgressView: UploadProgressView
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport with Navigation[AmendAnswers] with FileUploading {

  override val page: Page = UploadPage

  override protected def successRedirectUrl(uploadId: UploadId): Call = routes.UploadFormController.onProgress(uploadId)

  override protected def errorRedirectUrl(errorCode: String): Call = routes.UploadFormController.onError(errorCode)

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getAmendAnswersWithJourneyId flatMap { answersWithJourneyID =>
      initiateForm(answersWithJourneyID._2) map { upscanInitiateResponse =>
        uploadInitialView(upscanInitiateResponse, answersWithJourneyID._1, None)
      }
    }
  }

  def onProgress(uploadId: UploadId): Action[AnyContent] = identify.async { implicit request =>
    data.getAmendAnswersWithJourneyId flatMap { answersWithJourneyID =>
      uploadProgressTracker.getUploadResult(uploadId, answersWithJourneyID._2) flatMap {
        case Some(successUpload: UploadedFile) =>
          processSuccessfulUpload(successUpload)
        case Some(failed: Failed) =>
          Future(Redirect(routes.UploadFormController.onError(failed.errorCode)))
        case Some(_) =>
          Future(Ok(uploadProgressView(backLink(answersWithJourneyID._1), answersWithJourneyID._1.uploads)))
        case None => Future(Redirect(routes.UploadFormController.onError(UNKNOWN)))
      }
    }
  }

  def onError(errorCode: String): Action[AnyContent] = identify.async { implicit request =>
    data.getAmendAnswersWithJourneyId flatMap { answersWithJourneyID =>
      if (isFileMissingError(errorCode) && answersWithJourneyID._1.uploads.nonEmpty)
        Future(Redirect(routes.UploadFormController.onContinue()))
      else
        initiateForm(answersWithJourneyID._2) map { upscanInitiateResponse =>
          uploadInitialView(upscanInitiateResponse, answersWithJourneyID._1, Some(errorCode))
        }
    }
  }

  def onContinue(): Action[AnyContent] = identify.async { implicit request =>
    data.getAmendAnswers map { answers =>
      if (answers.uploads.isEmpty)
        Redirect(routes.UploadFormController.onError(ERROR_MISSING_FILE))
      else
        Redirect(nextPage(answers))
    }
  }

  def onRemove(documentReference: String): Action[AnyContent] = identify.async { implicit request =>
    data.updateAmendAnswers(removeDocument(documentReference)) map { _ =>
      Redirect(summaryAnchorUrl(routes.UploadFormController.onPageLoad()))
    }
  }

  def removeDocument: String => AmendAnswers => AmendAnswers = (ref: String) =>
    (userAnswers: AmendAnswers) => {
      val remainingFiles = userAnswers.uploads.filterNot(_.upscanReference == ref)
      userAnswers.copy(uploads = remainingFiles)
    }

  private def uploadInitialView(
    upscanInitiateResponse: UpscanInitiateResponse,
    answers: AmendAnswers,
    errorCode: Option[String]
  )(implicit request: IdentifierRequest[_]) = Ok(
    uploadFormView(upscanInitiateResponse, answers.uploads, errorCode.map(code => mapError(code)), backLink(answers))
  )

  private def processSuccessfulUpload(successUpload: UploadedFile)(implicit request: IdentifierRequest[_]) =
    data.getAmendAnswers flatMap { answers =>
      val uploads = answers.uploads
      if (uploads.exists(_.checksum == successUpload.checksum))
        Future(Redirect(routes.UploadFormController.onError(ERROR_DUPLICATE)))
      else
        data.updateAmendAnswers(answers => answers.copy(uploads = uploads :+ successUpload)) map {
          _ => Redirect(summaryAnchorUrl(routes.UploadFormController.onPageLoad()))
        }
    }

}
