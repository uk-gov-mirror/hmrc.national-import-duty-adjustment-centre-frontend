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
import org.mockito.Mockito.{reset, verify, when}
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{ControllerSpec, TestData}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.UpscanInitiateConnector
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ClaimType.AntiDumping
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UserAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UpscanNotification.Quarantine
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.{Failed, InProgress}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.UploadPage
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.UploadRepository
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.MongoBackedUploadProgressTracker
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.{UploadFormPage, UploadProgressPage}
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.Future

class UploadFormControllerSpec extends ControllerSpec with TestData {

  private val formPage     = mock[UploadFormPage]
  private val progressPage = mock[UploadProgressPage]

  private val repository              = mock[UploadRepository]
  private val upscanInitiateConnector = mock[UpscanInitiateConnector]
  private val appConfig               = instanceOf[AppConfig]
  private val uploadProgressTracker   = new MongoBackedUploadProgressTracker(repository)

  private def controller =
    new UploadFormController(
      stubMessagesControllerComponents(),
      fakeAuthorisedIdentifierAction,
      uploadProgressTracker,
      upscanInitiateConnector,
      cacheDataService,
      appConfig,
      navigator,
      formPage,
      progressPage
    )(executionContext)

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    when(upscanInitiateConnector.initiateV2(any(), any())(any())).thenReturn(Future.successful(upscanInitiateResponse))
    when(repository.add(any())).thenReturn(Future.successful(true))

    when(formPage.apply(any(), any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
    when(progressPage.apply()(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(formPage, progressPage, repository, upscanInitiateConnector)
    super.afterEach()
  }

  "onPageLoad" should {

    "initiate upscan and persist the result" in {
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe OK

      verify(upscanInitiateConnector).initiateV2(any(), any())(any())
      verify(repository).add(any())
    }

  }

  "showResult" should {

    "return page when upload in progress" in {
      when(repository.findByUploadId(uploadId)).thenReturn(Future.successful(Some(uploadResult(InProgress))))
      val result = controller.showResult(uploadId)(fakeGetRequest)

      status(result) mustBe OK
    }

    "redirect when upload failed" in {
      when(repository.findByUploadId(uploadId)).thenReturn(
        Future.successful(Some(uploadResult(Failed(Quarantine, "bad file"))))
      )
      val result = controller.showResult(uploadId)(fakeGetRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(
        controllers.makeclaim.routes.UploadFormController.showError(Quarantine.toString).url
      )
    }

    "update UserAnsers and redirect when upload succeeds" in {
      withCacheUserAnswers(Some(UserAnswers(claimType = Some(AntiDumping))))
      when(repository.findByUploadId(uploadId)).thenReturn(Future.successful(Some(uploadResult(uploadedFile))))
      val result = controller.showResult(uploadId)(fakeGetRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(navigator.nextPage(UploadPage, emptyAnswers).url)
    }

  }

  "showError" should {

    "initiate upscan and persist the result" in {
      val result = controller.showError("code")(fakeGetRequest)
      status(result) mustBe OK

      verify(upscanInitiateConnector).initiateV2(any(), any())(any())
      verify(repository).add(any())
    }

  }

}
