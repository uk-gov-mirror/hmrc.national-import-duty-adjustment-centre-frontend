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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.NIDACConnector
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{CreateClaimResponse, CreateClaimResult}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.CheckYourAnswersPage
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.Future
import scala.util.Random

class CheckYourAnswersControllerSpec extends ControllerSpec with TestData {

  val page: CheckYourAnswersPage = mock[CheckYourAnswersPage]
  val connector: NIDACConnector  = mock[NIDACConnector]
  val claimRef                   = Random.nextString(12)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    when(page.apply(any())(any(), any())).thenReturn(HtmlFormat.empty)
    when(connector.submitClaim(any())(any())).thenReturn(
      Future.successful(CreateClaimResponse("id", None, Some(CreateClaimResult(claimRef, Seq.empty))))
    )
  }

  override protected def afterEach(): Unit = {
    reset(page, connector)
    super.afterEach()
  }

  private val controller =
    new CheckYourAnswersController(
      stubMessagesControllerComponents(),
      fakeAuthorisedIdentifierAction,
      dataRequiredAction,
      connector,
      dataRepository,
      page
    )

  "GET" should {

    "return OK when user has answered all questions" in {
      withCacheUserAnswers(Some(completeAnswers))
      val result = controller.onPageLoad()(fakeGetRequest)

      status(result) mustBe Status.OK
    }

    "redirect to start when cache empty" in {
      withEmptyCache
      val result = controller.onPageLoad()(fakeGetRequest)

      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.StartController.start().url)
    }

    "error when answers missing" in {
      withCacheUserAnswers(Some(emptyAnswers))
      val result = controller.onPageLoad()(fakeGetRequest)
      intercept[Exception](status(result)).getMessage must startWith("missing answer")
    }
  }

  "POST" should {

    "submit and redirect to confirmation page" in {
      withCacheUserAnswers(Some(completeAnswers))
      val result = controller.onSubmit()(postRequest())

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ConfirmationController.onPageLoad().url)
    }

    "redirect to start when cache empty" in {
      withEmptyCache
      val result = controller.onSubmit()(postRequest())

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.StartController.start().url)
    }
  }
}
