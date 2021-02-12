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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.DutyPaidFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{DutyPaid, UserAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{
  CustomsDutyRepaymentPage,
  ImportVatRepaymentPage,
  OtherDutyRepaymentPage
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.DutyRepaymentView
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class DutyRepaymentControllerSpec extends ControllerSpec with TestData {

  private val page         = mock[DutyRepaymentView]
  private val formProvider = new DutyPaidFormProvider

  private def controller =
    new DutyRepaymentController(
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
    when(page.apply(any(), any(), any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(page)
    super.afterEach()
  }

  def theResponseForm: Form[DutyPaid] = {
    val captor = ArgumentCaptor.forClass(classOf[Form[DutyPaid]])
    verify(page).apply(captor.capture(), any(), any(), any())(any(), any())
    captor.getValue
  }

  "GET" should {

    "display page when cache is empty" in {
      val result = controller.onPageLoadCustomsDuty()(fakeGetRequest)
      status(result) mustBe Status.OK

      theResponseForm.value mustBe None
    }

    "display page when cache has answer" when {

      "loading customs duty" in {
        withCacheUserAnswers(UserAnswers(reclaimDutyPayments = reclaimDutyPayments))
        val result = controller.onPageLoadCustomsDuty()(fakeGetRequest)
        status(result) mustBe Status.OK

        theResponseForm.value mustBe Some(customsDutyRepaymentAnswer)
      }

      "loading import vat" in {
        withCacheUserAnswers(UserAnswers(reclaimDutyPayments = reclaimDutyPayments))
        val result = controller.onPageLoadImportVat()(fakeGetRequest)
        status(result) mustBe Status.OK

        theResponseForm.value mustBe Some(importVatRepaymentAnswer)
      }

      "loading other duty" in {
        withCacheUserAnswers(UserAnswers(reclaimDutyPayments = reclaimDutyPayments))
        val result = controller.onPageLoadOtherDuty()(fakeGetRequest)
        status(result) mustBe Status.OK

        theResponseForm.value mustBe Some(otherDutyRepaymentAnswer)
      }
    }
  }

  "POST" should {

    val validRequest = postRequest("actuallyPaid" -> "123", "shouldPaid" -> "23.99")

    val dutyPaid = DutyPaid("123", "23.99")

    "update cache and redirect when customs duty answer is submitted" in {

      withCacheUserAnswers(emptyAnswers)

      val result = controller.onSubmitCustomsDuty()(validRequest)
      status(result) mustEqual SEE_OTHER
      theUpdatedUserAnswers.reclaimDutyPayments mustBe Map(Customs.toString -> dutyPaid)
      redirectLocation(result) mustBe Some(navigator.nextPage(CustomsDutyRepaymentPage, theUpdatedUserAnswers).url)
    }

    "update cache and redirect when import VAT answer is submitted" in {

      withCacheUserAnswers(emptyAnswers)

      val result = controller.onSubmitImportVat()(validRequest)
      status(result) mustEqual SEE_OTHER
      theUpdatedUserAnswers.reclaimDutyPayments mustBe Map(Vat.toString -> dutyPaid)
      redirectLocation(result) mustBe Some(navigator.nextPage(ImportVatRepaymentPage, theUpdatedUserAnswers).url)
    }

    "update cache and redirect when other duty answer is submitted" in {

      withCacheUserAnswers(emptyAnswers)

      val result = controller.onSubmitOtherDuty()(validRequest)
      status(result) mustEqual SEE_OTHER
      theUpdatedUserAnswers.reclaimDutyPayments mustBe Map(Other.toString -> dutyPaid)
      redirectLocation(result) mustBe Some(navigator.nextPage(OtherDutyRepaymentPage, theUpdatedUserAnswers).url)
    }

    "return 400 (BAD REQUEST) when invalid data posted" in {

      val result = controller.onSubmitCustomsDuty()(postRequest())
      status(result) mustEqual BAD_REQUEST
    }

  }
}
