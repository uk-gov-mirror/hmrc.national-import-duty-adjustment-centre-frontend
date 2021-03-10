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

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import play.api.data.Form
import play.api.http.Status
import play.api.test.Helpers._
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{ControllerSpec, TestData}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.WhatDoYouWantToDoFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ToDoType
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ToDoType.{AmendClaim, NewClaim}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.FirstPage
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.WhatDoYouWantToDoPage
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

class WhatDoYouWantToDoControllerSpec extends ControllerSpec with TestData {

  private val page         = mock[WhatDoYouWantToDoPage]
  private val formProvider = new WhatDoYouWantToDoFormProvider

  private def controller =
    new WhatDoYouWantToDoController(
      fakeAuthorisedIdentifierAction,
      formProvider,
      stubMessagesControllerComponents(),
      navigator,
      amendNavigator,
      page
    )(executionContext)

  private def unauthorisdController =
    new WhatDoYouWantToDoController(
      fakeUnauthorisedIdentifierAction,
      formProvider,
      stubMessagesControllerComponents(),
      navigator,
      amendNavigator,
      page
    )(executionContext)

  def theResponseForm: Form[ToDoType] = {
    val captor = ArgumentCaptor.forClass(classOf[Form[ToDoType]])
    verify(page).apply(captor.capture())(any(), any())
    captor.getValue
  }

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    when(page.apply(any())(any(), any())).thenReturn(HtmlFormat.empty)
  }

  override protected def afterEach(): Unit = {
    reset(page)
    super.afterEach()
  }

  "User is authorised" when {
    "GET" should {
      "display page when is empty" in {
        val result = controller.onPageLoad()(fakeGetRequest)
        status(result) mustBe Status.OK
        theResponseForm.value mustBe None
      }
    }
  }

  "User is unauthorized" when {
    "GET" should {
      "redirect to unauthorised page" in {
        val result = unauthorisdController.onPageLoad()(fakeGetRequest)
        status(result) mustBe Status.SEE_OTHER
        redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
      }
    }
  }

  "POST" when {
    "new claim selected" should {
      "redirect to create claim journey" in {
        val validRequest = postRequest(("what_do_you_want_to_do", NewClaim.toString))
        val result       = controller.onSubmit()(validRequest)
        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(navigator.nextPage(FirstPage, emptyAnswers).url)
      }
    }

    "amend claim selected" should {
      "redirect to amend claim journey" in {
        val validRequest = postRequest(("what_do_you_want_to_do", AmendClaim.toString))
        val result       = controller.onSubmit()(validRequest)
        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(amendNavigator.nextPage(FirstPage, emptyAmendAnswers).url)
      }
    }

    "nothing selected" should {
      "return 400 (BAD REQUEST) when invalid data posted" in {
        val result = controller.onSubmit()(postRequest())
        status(result) mustEqual BAD_REQUEST
      }
    }
  }
}
