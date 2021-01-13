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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.ControllerSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.ClaimTypeFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ClaimType.{AntiDumping, Tomato147s}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{ClaimType, UserAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ClaimTypePage
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class ClaimTypeControllerSpec extends ControllerSpec {

  private val page         = mock[ClaimTypePage]
  private val formProvider = new ClaimTypeFormProvider

  private def controller =
    new ClaimTypeController(
      sessionRepository,
      fakeAuthorisedIdentifierAction,
      dataRetrievalAction,
      formProvider,
      stubMessagesControllerComponents(),
      navigator,
      page
    )(executionContext)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    withEmptyCache
    when(page.apply(any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(page)
    super.afterEach()
  }

  def theResponseForm: Form[ClaimType] = {
    val captor = ArgumentCaptor.forClass(classOf[Form[ClaimType]])
    verify(page).apply(captor.capture())(any(), any())
    captor.getValue
  }

  "GET" should {

    "display page when cache is empty" in {
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe Status.OK

      theResponseForm.value mustBe None
    }

    "display page when cache has answer" in {
      withCachedData(Some(UserAnswers("id", claimType = Some(AntiDumping))))
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe Status.OK

      theResponseForm.value mustBe Some(AntiDumping)
    }
  }

  "POST" should {

    val validRequest = postRequest(("claim_type", Tomato147s.toString))

    "redirect when valid answer is submitted" in {

      val result = controller.onSubmit()(validRequest)
      status(result) mustEqual SEE_OTHER

      redirectLocation(result) mustBe Some(routes.CheckYourAnswersController.onPageLoad().url)
    }

    "update cache when valid answer is submitted" in {

      val result = controller.onSubmit()(validRequest)
      status(result) mustEqual SEE_OTHER

      theUpdatedCache.claimType mustBe Some(Tomato147s)
    }

    "return 400 (BAD REQUEST) when invalid data posted" in {

      val result = controller.onSubmit()(postRequest())
      status(result) mustEqual BAD_REQUEST
    }

  }
}
