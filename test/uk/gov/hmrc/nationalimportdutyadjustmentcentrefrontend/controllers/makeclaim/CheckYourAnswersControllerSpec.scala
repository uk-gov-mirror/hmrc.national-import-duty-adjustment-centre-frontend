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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.ClaimService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.{
  CheckYourAnswersView,
  CheckYourAnswersWithMissingView
}
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.Future
import scala.util.Random

class CheckYourAnswersControllerSpec extends ControllerSpec with TestData {

  val cyaView: CheckYourAnswersView              = mock[CheckYourAnswersView]
  val errorView: CheckYourAnswersWithMissingView = mock[CheckYourAnswersWithMissingView]
  val service: ClaimService                      = mock[ClaimService]
  val claimRef                                   = Random.nextString(12)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    when(cyaView.apply(any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
    when(errorView.apply(any(), any())(any(), any())).thenReturn(HtmlFormat.empty)
    when(service.submitClaim(any())(any(), any())).thenReturn(
      Future.successful(
        CreateClaimResponse("id", Some(fixedInstant), None, Some(CreateClaimResult(claimRef, Seq.empty)))
      )
    )
  }

  override protected def afterEach(): Unit = {
    reset(cyaView, service)
    super.afterEach()
  }

  private val controller =
    new CheckYourAnswersController(
      stubMessagesControllerComponents(),
      fakeAuthorisedIdentifierAction,
      cacheDataService,
      service,
      navigator,
      cyaView,
      errorView
    )

  "onPageLoad" should {

    "return OK when user has answered all questions" in {
      withCacheCreateAnswers(completeAnswers)
      val result = controller.onPageLoad()(fakeGetRequest)

      status(result) mustBe Status.OK
    }

    "return BAD REQUEST when cache empty" in {
      withEmptyCache
      val result = controller.onPageLoad()(fakeGetRequest)

      status(result) mustBe Status.BAD_REQUEST
    }

    "return BAD REQUEST when answers missing" in {
      withCacheCreateAnswers(emptyAnswers)

      val result = controller.onPageLoad()(fakeGetRequest)

      status(result) mustBe Status.BAD_REQUEST
    }

    "return BAD REQUEST when answers incomplete" in {
      withCacheCreateAnswers(completeAnswers.copy(uploads = Seq.empty))

      val result = controller.onPageLoad()(fakeGetRequest)

      status(result) mustBe Status.BAD_REQUEST
    }
  }

  "onSubmit" should {

    "submit and redirect to confirmation page" in {
      withCacheCreateAnswers(completeAnswers)
      val result = controller.onSubmit()(postRequest())

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustBe Some(routes.ConfirmationController.onPageLoad().url)
    }

    "return BAD REQUEST when cache empty" in {
      withEmptyCache
      val result = controller.onSubmit()(postRequest())

      status(result) mustBe Status.BAD_REQUEST
    }
  }

  "onResolve" should {

    "redirect to first missing answer when answers incomplete" in {
      withCacheCreateAnswers(completeAnswers.copy(uploads = Seq.empty))
      val result = controller.onResolve()(postRequest())

      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(controllers.makeclaim.routes.UploadFormController.onPageLoad().url)
    }
  }
}
