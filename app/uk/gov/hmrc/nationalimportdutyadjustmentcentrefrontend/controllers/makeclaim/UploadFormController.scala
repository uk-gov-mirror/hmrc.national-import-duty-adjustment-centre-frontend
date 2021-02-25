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
import play.api.data.FormError
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.{Reference, UpscanInitiateConnector}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.CreateAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.{Failed, UploadedFile}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{JourneyId, UploadId}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.CreateNavigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{Page, UploadPage}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.{CacheDataService, UploadProgressTracker}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.viewmodels.NavigatorBack
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.{UploadFormView, UploadProgressView}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadFormController @Inject() (
  mcc: MessagesControllerComponents,
  identify: IdentifierAction,
  uploadProgressTracker: UploadProgressTracker,
  upscanInitiateConnector: UpscanInitiateConnector,
  data: CacheDataService,
  appConfig: AppConfig,
  val navigator: CreateNavigator,
  uploadFormView: UploadFormView,
  uploadProgressView: UploadProgressView
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport with Navigation[CreateAnswers] {

  override val page: Page = UploadPage

  override def backLink: CreateAnswers => NavigatorBack = (answers: CreateAnswers) =>
    answers.uploads match {
      case files if files.nonEmpty => NavigatorBack(Some(routes.UploadFormSummaryController.onPageLoad()))
      case _                       => super.backLink(answers)
    }

  private val errorRedirectUrl =
    appConfig.upscan.redirectBase + "/national-import-duty-adjustment-centre/create/upload-supporting-documents/error"

  private def successRedirectUrl(uploadId: UploadId) =
    appConfig.upscan.redirectBase + routes.UploadFormController.onProgress(uploadId).url

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswersWithJourneyId flatMap { answersWithJourneyID =>
      initiateForm(answersWithJourneyID._1, answersWithJourneyID._2)
    }
  }

  def onProgress(uploadId: UploadId): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswersWithJourneyId flatMap { answersWithJourneyID =>
      uploadProgressTracker.getUploadResult(uploadId, answersWithJourneyID._2) flatMap {
        case Some(successUpload: UploadedFile) =>
          processSuccessfulUpload(successUpload)
        case Some(failed: Failed) =>
          Future(Redirect(controllers.makeclaim.routes.UploadFormController.onError(failed.errorCode)))
        case Some(_) =>
          Future(Ok(uploadProgressView(answersWithJourneyID._1.claimType, backLink(answersWithJourneyID._1))))
        case None => Future(Redirect(controllers.makeclaim.routes.UploadFormController.onError("NOT_FOUND")))
      }
    }
  }

  def onError(errorCode: String): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswersWithJourneyId flatMap { answersWithJourneyID =>
      initiateForm(answersWithJourneyID._1, answersWithJourneyID._2, Some(mapError(errorCode)))
    }
  }

  private def initiateForm(answers: CreateAnswers, journeyId: JourneyId, maybeError: Option[FormError] = None)(implicit
    request: IdentifierRequest[_]
  ) = {
    val uploadId = UploadId.generate
    for {
      upscanInitiateResponse <- upscanInitiateConnector.initiateV2(
        journeyId,
        Some(successRedirectUrl(uploadId)),
        Some(errorRedirectUrl)
      )
      _ <- uploadProgressTracker.requestUpload(
        uploadId,
        journeyId,
        Reference(upscanInitiateResponse.fileReference.reference)
      )
    } yield Ok(
      uploadFormView(upscanInitiateResponse, answers.claimType, answers.uploads.isEmpty, maybeError, backLink(answers))
    )
  }

  private def processSuccessfulUpload(successUpload: UploadedFile)(implicit request: IdentifierRequest[_]) =
    data.getCreateAnswers flatMap { answers =>
      val uploads = answers.uploads
      if (uploads.exists(_.checksum == successUpload.checksum))
        Future(Redirect(controllers.makeclaim.routes.UploadFormController.onError("DUPLICATE")))
      else
        data.updateCreateAnswers(answers => answers.copy(uploads = uploads :+ successUpload)) map {
          updatedAnswers => Redirect(nextPage(updatedAnswers))
        }
    }

  private def mapError(code: String): FormError = {
    def error(message: String) = FormError("upload-file", message)
    code match {
      case "400" | "InvalidArgument" => error("error.file-upload.required")
      case "InternalError"           => error("error.file-upload.try-again")
      case "EntityTooLarge"          => error("error.file-upload.invalid-size-large")
      case "EntityTooSmall"          => error("error.file-upload.invalid-size-small")
      case "QUARANTINE"              => error("error.file-upload.quarantine")
      case "REJECTED"                => error("error.file-upload.invalid-type")
      case "DUPLICATE"               => error("error.file-upload.duplicate")
      case _                         => error("error.file-upload.unknown")
    }
  }

}
