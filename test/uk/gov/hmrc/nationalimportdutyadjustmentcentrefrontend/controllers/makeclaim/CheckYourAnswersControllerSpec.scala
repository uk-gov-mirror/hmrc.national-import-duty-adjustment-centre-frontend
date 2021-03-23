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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateClaimResponse, CreateClaimResult}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.ClaimService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.CheckYourAnswersView
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.Future
import scala.util.Random

class CheckYourAnswersControllerSpec extends ControllerSpec with TestData {

  val page: CheckYourAnswersView = mock[CheckYourAnswersView]
  val service: ClaimService      = mock[ClaimService]
  val claimRef                   = Random.nextString(12)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    when(page.apply(any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
    when(service.submitClaim(any(), any())(any(), any())).thenReturn(
      Future.successful(CreateClaimResponse("id", None, Some(CreateClaimResult(claimRef, Seq.empty))))
    )
  }

  override protected def afterEach(): Unit = {
    reset(page, service)
    super.afterEach()
  }

  private val controller =
    new CheckYourAnswersController(
      stubMessagesControllerComponents(),
      fakeAuthorisedIdentifierAction,
      cacheDataService,
      service,
      navigator,
      page
    )

  "GET" should {

    "return OK when user has answered all questions" in {
      withCacheCreateAnswers(completeAnswers)
      val result = controller.onPageLoad()(fakeGetRequest)

      status(result) mustBe Status.OK
    }

    "redirect to start when cache empty" in {
      withEmptyCache
      val result = controller.onPageLoad()(fakeGetRequest)

      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.StartController.start().url)
    }

    "redirect to start when answers missing" in {
      withCacheCreateAnswers(emptyAnswers)

      val result = controller.onPageLoad()(fakeGetRequest)

      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.routes.StartController.start().url)
    }
  }

  "POST" should {

    "submit and redirect to confirmation page" in {
      withCacheCreateAnswers(completeAnswers)
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
