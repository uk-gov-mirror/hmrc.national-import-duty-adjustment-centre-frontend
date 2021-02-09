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
import org.mockito.Mockito.{reset, when}
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{ControllerSpec, TestData}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UserAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{
  BankDetailsPage,
  ItemNumbersPage,
  UploadSummaryPage
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.UploadSummaryPage
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class UploadFormSummaryControllerSpec extends ControllerSpec with TestData {

  private val formPage = mock[UploadSummaryPage]

  private def controller =
    new UploadFormSummaryController(
      stubMessagesControllerComponents(),
      fakeAuthorisedIdentifierAction,
      cacheDataService,
      navigator,
      formPage
    )(executionContext)

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    withCacheUserAnswers(emptyAnswers)

    when(formPage.apply(any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(formPage)
    super.afterEach()
  }

  "GET" should {

    "display page when cache contains uploads" in {

      withCacheUserAnswers(UserAnswers(uploads = Some(Seq(uploadAnswer))))
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe Status.OK
    }

    "redirect when cache does not contain uploads" in {
      withCacheUserAnswers(UserAnswers(uploads = Some(Seq.empty)))
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.makeclaim.routes.UploadFormController.onPageLoad().url)
    }

  }

  "POST" should {

    "redirect to next question" in {

      val result = controller.onSubmit()(postRequest())
      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(navigator.nextPage(UploadSummaryPage, emptyAnswers).url)
    }
  }

}
