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

import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.Reference
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UpscanNotificationSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.{UploadId, UploadStatus}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.UploadProgressTracker

import scala.concurrent.Future

class UploadCallbackControllerSpec extends UnitSpec with GuiceOneAppPerSuite with TestData {

  private val dummyProgressTracker = new UploadProgressTracker {
    override def requestUpload(uploadId: UploadId, fileReference: Reference): Future[Boolean] = Future.successful(true)

    override def registerUploadResult(reference: Reference, uploadStatus: UploadStatus): Future[Boolean] =
      Future.successful(true)

    override def getUploadResult(id: UploadId): Future[Option[UploadStatus]] = Future.successful(None)
  }

  override lazy val app: Application = GuiceApplicationBuilder()
    .overrides(bind[UploadProgressTracker].to(dummyProgressTracker))
    .build()

  val post = FakeRequest("POST", "/national-import-duty-adjustment-centre/upscan-callback")

  "UploadCallbackController" should {

    "return 204" when {

      "success notification is received" in {

        val request = post
          .withHeaders((CONTENT_TYPE, JSON))
          .withJsonBody(Json.parse(UpscanNotificationSpec.successNotificationBody))

        val result: Future[Result] = route(app, request).get

        status(result) must be(NO_CONTENT)
      }

      "failed notification is recieved" in {

        val request = post
          .withHeaders((CONTENT_TYPE, JSON))
          .withJsonBody(Json.parse(UpscanNotificationSpec.failedNotificationBody))

        val result: Future[Result] = route(app, request).get

        status(result) must be(NO_CONTENT)
      }
    }

  }
}
