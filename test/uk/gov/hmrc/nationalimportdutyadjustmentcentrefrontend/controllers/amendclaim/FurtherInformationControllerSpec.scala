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

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import play.api.data.Form
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{ControllerSpec, TestData}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.amend.FurtherInformationFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.{AmendAnswers, FurtherInformation}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.FurtherInformationPage
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendclaim.FurtherInformationView
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class FurtherInformationControllerSpec extends ControllerSpec with TestData {

  private val page         = mock[FurtherInformationView]
  private val formProvider = new FurtherInformationFormProvider

  private def controller =
    new FurtherInformationController(
      fakeAuthorisedIdentifierAction,
      cacheDataService,
      formProvider,
      stubMessagesControllerComponents(),
      amendNavigator,
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

  def theResponseForm: Form[FurtherInformation] = {
    val captor = ArgumentCaptor.forClass(classOf[Form[FurtherInformation]])
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
      withCacheAmendAnswers(AmendAnswers(furtherInformation = Some(furtherInformationAnswer)))
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe Status.OK

      theResponseForm.value mustBe Some(furtherInformationAnswer)
    }
  }

  "POST" should {

    val validRequest = postRequest("furtherInformation" -> furtherInformationAnswer.info)

    "update cache and redirect when valid answer is submitted" in {

      withCacheAmendAnswers(emptyAmendAnswers)

      val result = controller.onSubmit()(validRequest)
      status(result) mustEqual SEE_OTHER
      theUpdatedAmendAnswers.furtherInformation mustBe Some(furtherInformationAnswer)
      redirectLocation(result) mustBe Some(amendNavigator.nextPage(FurtherInformationPage, theUpdatedAmendAnswers).url)
    }

    "return 400 (BAD REQUEST) when invalid data posted" in {

      val result = controller.onSubmit()(postRequest())
      status(result) mustEqual BAD_REQUEST
    }

  }
}
