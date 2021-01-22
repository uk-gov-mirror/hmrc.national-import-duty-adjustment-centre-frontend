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
import play.api.libs.json.JsValue
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.Reference
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.UploadProgressTracker
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UploadCallbackController @Inject() (
  uploadProgressTracker: UploadProgressTracker,
  val controllerComponents: MessagesControllerComponents
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  val callback: Action[JsValue] = Action.async(parse.json) { implicit request =>
    withJsonBody[UpscanNotification] { feedback: UpscanNotification =>
      handleCallback(feedback).map(_ => NoContent)
    } recover {
      case e: IllegalArgumentException => BadRequest
      case e                           => InternalServerError
    }
  }

  def handleCallback(callback: UpscanNotification): Future[Boolean] = {

    val uploadStatus = callback match {
      case ready: UpscanFileReady =>
        UploadedFile(
          ready.reference,
          ready.downloadUrl,
          ready.uploadDetails.uploadTimestamp,
          ready.uploadDetails.checksum,
          ready.uploadDetails.fileName,
          ready.uploadDetails.fileMimeType
        )
      case failed: UpscanFileFailed =>
        Failed(failed.failureDetails.failureReason, failed.failureDetails.message)
    }

    uploadProgressTracker.registerUploadResult(Reference(callback.reference), uploadStatus)
  }

}
