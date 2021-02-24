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

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import play.api.data.Form
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{ControllerSpec, TestData}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.ClaimReasonFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{ClaimReason, CreateAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.ClaimReasonPage
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ClaimReasonView
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class ClaimReasonControllerSpec extends ControllerSpec with TestData {

  private val page         = mock[ClaimReasonView]
  private val formProvider = new ClaimReasonFormProvider

  private def controller =
    new ClaimReasonController(
      fakeAuthorisedIdentifierAction,
      cacheDataService,
      formProvider,
      stubMessagesControllerComponents(),
      navigator,
      page
    )(executionContext)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    withEmptyCache()
    when(page.apply(any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(page)
    super.afterEach()
  }

  def theResponseForm: Form[ClaimReason] = {
    val captor = ArgumentCaptor.forClass(classOf[Form[ClaimReason]])
    verify(page).apply(captor.capture(), any())(any(), any())
    captor.getValue
  }

  "GET" should {

    "display page when cache is empty" in {
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe Status.OK

      theResponseForm.value mustBe None
    }

    "display page when cache has answer" in {
      withCacheCreateAnswers(CreateAnswers(claimReason = Some(claimReasonAnswer)))
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe Status.OK

      theResponseForm.value mustBe Some(claimReasonAnswer)
    }
  }

  "POST" should {

    val validRequest = postRequest("claimReason" -> claimReasonAnswer.reason)

    "update cache and redirect when valid answer is submitted" in {

      withCacheCreateAnswers(emptyAnswers)

      val result = controller.onSubmit()(validRequest)
      status(result) mustEqual SEE_OTHER
      theUpdatedCreateAnswers.claimReason mustBe Some(claimReasonAnswer)
      redirectLocation(result) mustBe Some(navigator.nextPage(ClaimReasonPage, theUpdatedCreateAnswers).url)
    }

    "return 400 (BAD REQUEST) when invalid data posted" in {

      val result = controller.onSubmit()(postRequest())
      status(result) mustEqual BAD_REQUEST
    }

  }
}
