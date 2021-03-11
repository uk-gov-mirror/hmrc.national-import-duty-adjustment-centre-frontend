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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UpscanNotificationSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.{CacheDataRepository, UploadRepository}

import scala.concurrent.{ExecutionContext, Future}

class UploadCallbackControllerSpec
    extends UnitSpec with MockitoSugar with GuiceOneAppPerSuite with BeforeAndAfterEach with TestData {

  implicit val ec: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  private val mockUploadRepository    = mock[UploadRepository]
  private val mockCacheDataRepository = mock[CacheDataRepository]

  override lazy val app: Application = GuiceApplicationBuilder()
    .overrides(bind[UploadRepository].to(mockUploadRepository), bind[CacheDataRepository].to(mockCacheDataRepository))
    .build()

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    when(mockUploadRepository.updateStatus(any(), any(), any())).thenReturn(Future.successful(uploadInProgress))
  }

  override protected def afterEach(): Unit = {
    reset(mockUploadRepository)
    super.afterEach()
  }

  val post = FakeRequest(
    "POST",
    s"/apply-for-refund-import-duty-paid-on-deposit-or-guarantee/upscan-callback?journeyId=$journeyId"
  )

  "UploadCallbackController" should {

    "return 204" when {

      "success notification is received" in {

        val request = post
          .withHeaders((CONTENT_TYPE, JSON))
          .withJsonBody(Json.parse(UpscanNotificationSpec.successNotificationBody))

        val result: Future[Result] = route(app, request).get

        status(result) must be(NO_CONTENT)
      }

      "failed notification is received" in {

        val request = post
          .withHeaders((CONTENT_TYPE, JSON))
          .withJsonBody(Json.parse(UpscanNotificationSpec.failedNotificationBody))

        val result: Future[Result] = route(app, request).get

        status(result) must be(NO_CONTENT)
      }
    }

  }
}
