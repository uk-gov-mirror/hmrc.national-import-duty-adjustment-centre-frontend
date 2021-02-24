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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.EntryDetailsFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, EntryDetails}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.EntryDetailsPage
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.EntryDetailsView
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class EntryDetailsControllerSpec extends ControllerSpec with TestData {

  private val page         = mock[EntryDetailsView]
  private val formProvider = new EntryDetailsFormProvider

  private def controller =
    new EntryDetailsController(
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

  def theResponseForm: Form[EntryDetails] = {
    val captor = ArgumentCaptor.forClass(classOf[Form[EntryDetails]])
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
      withCacheCreateAnswers(CreateAnswers(entryDetails = Some(entryDetailsAnswer)))
      val result = controller.onPageLoad()(fakeGetRequest)
      status(result) mustBe Status.OK

      theResponseForm.value mustBe Some(entryDetailsAnswer)
    }
  }

  "POST" should {

    val validRequest = postRequest(
      "entryProcessingUnit" -> entryDetailsAnswer.entryProcessingUnit,
      "entryNumber"         -> entryDetailsAnswer.entryNumber,
      "entryDate"           -> entryDetailsAnswer.entryDate.getDayOfMonth.toString,
      "entryDate.month"     -> entryDetailsAnswer.entryDate.getMonthValue.toString,
      "entryDate.year"      -> entryDetailsAnswer.entryDate.getYear.toString
    )

    "update cache and redirect when valid answer is submitted" in {

      withCacheCreateAnswers(emptyAnswers)

      val result = controller.onSubmit()(validRequest)
      status(result) mustEqual SEE_OTHER
      theUpdatedCreateAnswers.entryDetails mustBe Some(entryDetailsAnswer)
      redirectLocation(result) mustBe Some(navigator.nextPage(EntryDetailsPage, emptyAnswers).url)
    }

    "return 400 (BAD REQUEST) when invalid data posted" in {

      val result = controller.onSubmit()(postRequest())
      status(result) mustEqual BAD_REQUEST
    }

  }
}
