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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{never, reset, verify, when}
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{ControllerSpec, TestData}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.UpscanInitiateConnector
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.JourneyId
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadStatus
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UpscanNotification.Quarantine
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.UploadRepository
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.MongoBackedUploadProgressTracker
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.{UploadFormPage, UploadProgressPage}
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.Future

class UploadFormControllerSpec extends ControllerSpec with TestData {

  private val formPage     = mock[UploadFormPage]
  private val progressPage = mock[UploadProgressPage]

  private val mockInitiateConnector = mock[UpscanInitiateConnector]
  private val appConfig             = instanceOf[AppConfig]
  private val mockUploadRepository  = mock[UploadRepository]
  private val progressTracker       = new MongoBackedUploadProgressTracker(mockUploadRepository)

  private def controller =
    new UploadFormController(
      stubMessagesControllerComponents(),
      fakeAuthorisedIdentifierAction,
      progressTracker,
      mockInitiateConnector,
      cacheDataService,
      appConfig,
      navigator,
      formPage,
      progressPage
    )(executionContext)

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    withCacheUserAnswers(emptyAnswers)

    when(mockInitiateConnector.initiateV2(any[JourneyId], any(), any())(any())).thenReturn(
      Future.successful(upscanInitiateResponse)
    )

    when(mockUploadRepository.add(any())).thenReturn(Future.successful(true))

    when(formPage.apply(any(), any(), any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
    when(progressPage.apply(any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(formPage, progressPage, mockInitiateConnector, mockUploadRepository)
    super.afterEach()
  }

  private def givenUploadStatus(status: UploadStatus): Unit =
    when(mockUploadRepository.findUploadDetails(any(), any())).thenReturn(Future.successful(Some(uploadResult(status))))

  "onPageLoad" should {

    "initiate upscan and persist the result" in {
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe OK

      verify(mockInitiateConnector).initiateV2(any(), any(), any())(any())
      verify(mockUploadRepository).add(any())
    }

  }

  "onProgress" should {

    "return page when upload in progress" in {

      givenUploadStatus(uploadInProgress)
      val result = controller.onProgress(uploadId)(fakeGetRequest)

      status(result) mustBe OK
    }

    "redirect when upload failed" in {

      givenUploadStatus(uploadFailed)
      val result = controller.onProgress(uploadId)(fakeGetRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(
        controllers.makeclaim.routes.UploadFormController.onError(Quarantine.toString).url
      )
    }

    "redirect when uploading a duplicate file" in {

      withCacheUserAnswers(completeAnswers.copy(uploads = Some(Seq(uploadAnswer))))
      givenUploadStatus(uploadFileSuccess)
      val result = controller.onProgress(uploadId)(fakeGetRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.makeclaim.routes.UploadFormController.onError("DUPLICATE").url)

      verify(dataRepository, never()).set(any())
    }

    "update UserAnswers and redirect to summary when upload succeeds" in {

      givenUploadStatus(uploadFileSuccess)
      val result = controller.onProgress(uploadId)(fakeGetRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.makeclaim.routes.UploadFormSummaryController.onPageLoad().url)

      theUpdatedUserAnswers.uploads mustBe Some(Seq(uploadFileSuccess))
    }

  }

  "onError" should {

    "initiate upscan and persist the result" in {
      val result = controller.onError("code")(fakeGetRequest)
      status(result) mustBe OK

      verify(mockInitiateConnector).initiateV2(any(), any(), any())(any())
      verify(mockUploadRepository).add(any())
    }

  }

}
