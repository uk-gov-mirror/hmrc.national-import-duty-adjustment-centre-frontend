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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{verify, when}
import play.api.http.Status
import play.api.test.Helpers._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.ControllerSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.play.bootstrap.tools.Stubs.stubMessagesControllerComponents

import scala.concurrent.Future

class SignOutControllerSpec extends ControllerSpec {

  val appConfig: AppConfig = instanceOf[AppConfig]

  private def controller(identifyAction: IdentifierAction) =
    new SignOutController(stubMessagesControllerComponents(), identifyAction, dataRepository, appConfig)

  "GET /sign-out" should {

    "sign out user when user is authorised" in {
      when(dataRepository.delete(any())).thenReturn(Future.successful(()))
      val result = controller(fakeAuthorisedIdentifierAction).signOut(fakeGetRequest)

      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(appConfig.signOutUrl)
      verify(dataRepository).delete(any())
    }

    "redirect when user is unauthorised" in {
      val result = controller(fakeUnauthorisedIdentifierAction).signOut(fakeGetRequest)
      status(result) mustBe Status.SEE_OTHER
      redirectLocation(result) mustBe Some(routes.UnauthorisedController.onPageLoad().url)
    }
  }

}
