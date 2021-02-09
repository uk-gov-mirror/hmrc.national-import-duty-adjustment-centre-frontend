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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.{Failed, UploadedFile}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{JourneyId, UploadId, UserAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.{CacheDataService, UploadProgressTracker}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.{UploadFormPage, UploadProgressPage}
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
  uploadFormPage: UploadFormPage,
  uploadProgressPage: UploadProgressPage
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport {

  private val errorRedirectUrl =
    appConfig.upscan.redirectBase + "/national-import-duty-adjustment-centre/upload-documents/error"

  private def successRedirectUrl(uploadId: UploadId) =
    appConfig.upscan.redirectBase + routes.UploadFormController.onProgress(uploadId).url

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getAnswers flatMap { answers =>
      initiateForm(answers)
    }
  }

  def onProgress(uploadId: UploadId): Action[AnyContent] = identify.async { implicit request =>
    data.getAnswers flatMap { answers =>
      uploadProgressTracker.getUploadResult(uploadId, answers.journeyId) flatMap {
        case Some(successUpload: UploadedFile) =>
          data.updateAnswers(answers => addUpload(answers, successUpload)) map {
            _ => Redirect(controllers.makeclaim.routes.UploadFormSummaryController.onPageLoad())
          }
        case Some(failed: Failed) =>
          Future(Redirect(controllers.makeclaim.routes.UploadFormController.onError(failed.errorCode)))
        case Some(_) => Future(Ok(uploadProgressPage(answers.claimType)))
        case None    => Future(Redirect(controllers.makeclaim.routes.UploadFormController.onError("NOT_FOUND")))
      }
    }
  }

  def onError(errorCode: String): Action[AnyContent] = identify.async { implicit request =>
    data.getAnswers flatMap { answers =>
      initiateForm(answers, Some(mapError(errorCode)))
    }
  }

  private def initiateForm(answers: UserAnswers, maybeError: Option[FormError] = None)(implicit
    request: IdentifierRequest[_]
  ) = {
    val uploadId = UploadId.generate
    for {
      upscanInitiateResponse <- upscanInitiateConnector.initiateV2(
        answers.journeyId: JourneyId,
        Some(successRedirectUrl(uploadId)),
        Some(errorRedirectUrl)
      )
      _ <- uploadProgressTracker.requestUpload(
        uploadId,
        answers.journeyId,
        Reference(upscanInitiateResponse.fileReference.reference)
      )
    } yield Ok(uploadFormPage(upscanInitiateResponse, answers.claimType, answers.uploads.forall(_.isEmpty), maybeError))
  }

  private def addUpload(userAnswers: UserAnswers, successUpload: UploadedFile) = {

    /**
      * TODO - when multiple file uploads are supported ...
      * ...replace code below with
      * val uploads: Seq[UploadedFile] = userAnswers.uploads.getOrElse(Seq.empty)
      */
    val uploads: Seq[UploadedFile] = Seq.empty
    userAnswers.copy(uploads = Some(uploads :+ successUpload))
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
      case _                         => error("error.file-upload.unknown")
    }
  }

}
