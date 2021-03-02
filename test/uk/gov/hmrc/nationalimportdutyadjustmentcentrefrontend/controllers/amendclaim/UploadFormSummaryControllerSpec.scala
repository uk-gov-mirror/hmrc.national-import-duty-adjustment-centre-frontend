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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{ControllerSpec, TestData}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.YesNoFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.UploadSummaryPage
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendclaim.UploadSummaryView
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class UploadFormSummaryControllerSpec extends ControllerSpec with TestData {

  private val formPage     = mock[UploadSummaryView]
  private val formProvider = new YesNoFormProvider

  private def controller =
    new UploadFormSummaryController(
      stubMessagesControllerComponents(),
      fakeAuthorisedIdentifierAction,
      cacheDataService,
      formProvider,
      amendNavigator,
      formPage
    )(executionContext)

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    withCacheCreateAnswers(emptyAnswers)

    when(formPage.apply(any(), any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(formPage)
    super.afterEach()
  }

  "onPageLoad" should {

    "display page when cache contains uploads" in {

      withCacheAmendAnswers(AmendAnswers(uploads = Seq(uploadAnswer)))
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe Status.OK
    }

    "redirect when cache does not contain uploads" in {
      withCacheAmendAnswers(AmendAnswers(uploads = Seq.empty))
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.AttachMoreDocumentsController.onPageLoad().url)
    }

  }

  "onSubmit" should {

    "redirect to document upload when user wants to upload another" in {
      val result = controller.onSubmit()(postRequest(("yesOrNo", "true")))
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.UploadFormController.onPageLoad().url)
    }

    "redirect to next question when user does not want to upload another" in {
      val result = controller.onSubmit()(postRequest(("yesOrNo", "false")))
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(amendNavigator.nextPage(UploadSummaryPage, emptyAmendAnswers).url)
    }

    "error when user does answer the question" in {
      val result = controller.onSubmit()(postRequest())
      status(result) mustEqual BAD_REQUEST
    }
  }

  "onDelete" should {

    "remove uploaded document" in {
      withCacheAmendAnswers(AmendAnswers(uploads = Seq(uploadAnswer, uploadAnswer2)))
      val result = controller.onRemove(uploadAnswer.upscanReference)(postRequest())
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.UploadFormSummaryController.onPageLoad().url)

      theUpdatedAmendAnswers.uploads mustBe Seq(uploadAnswer2)
    }

  }

}
