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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UserAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.{Failed, UploadId, UploadedFile}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.Navigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.UploadPage
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
  navigator: Navigator,
  uploadFormPage: UploadFormPage,
  uploadProgressPage: UploadProgressPage
)(implicit ec: ExecutionContext)
    extends FrontendController(mcc) with I18nSupport {

  private val errorRedirectUrl =
    appConfig.upscan.callbackBase + "/national-import-duty-adjustment-centre/upload-documents/error"

  private def successRedirectUrl(uploadId: UploadId) =
    appConfig.upscan.callbackBase + routes.UploadFormController.showResult(uploadId).url

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    initiateForm()
  }

  def showResult(uploadId: UploadId): Action[AnyContent] = identify.async { implicit request =>
    uploadProgressTracker.getUploadResult(uploadId) flatMap {
      case Some(successUpload: UploadedFile) =>
        data.updateAnswers(answers => addUpload(answers, successUpload)) map {
          updatedAnswers => Redirect(navigator.nextPage(UploadPage, updatedAnswers))
        }
      case Some(failed: Failed) =>
        Future(Redirect(controllers.makeclaim.routes.UploadFormController.showError(failed.errorCode)))
      case Some(_) => Future(Ok(uploadProgressPage()))
      case None    => Future(BadRequest(s"Upload with id $uploadId not found"))
    }
  }

  def showError(errorCode: String): Action[AnyContent] =
    identify.async {
      implicit request =>
        initiateForm(Some(mapError(errorCode)))
    }

  private def initiateForm(maybeError: Option[FormError] = None)(implicit request: IdentifierRequest[_]) = {
    val uploadId = UploadId.generate
    for {
      upscanInitiateResponse <- upscanInitiateConnector.initiateV2(
        Some(successRedirectUrl(uploadId)),
        Some(errorRedirectUrl)
      )
      _ <- uploadProgressTracker.requestUpload(uploadId, Reference(upscanInitiateResponse.fileReference.reference))
    } yield Ok(uploadFormPage(upscanInitiateResponse, maybeError, appConfig))
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
