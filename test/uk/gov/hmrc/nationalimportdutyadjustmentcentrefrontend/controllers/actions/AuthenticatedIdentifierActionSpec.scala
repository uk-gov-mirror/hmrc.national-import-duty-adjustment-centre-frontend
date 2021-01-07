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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions

import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.{BodyParsers, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.{redirectLocation, _}
import uk.gov.hmrc.auth.core.authorise.Predicate
import uk.gov.hmrc.auth.core.retrieve.Retrieval
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ExecutionContext, Future}

class AuthActionSpec extends UnitSpec with MockitoSugar with Injector {

  val appConfig: AppConfig        = instanceOf[AppConfig]
  val parser: BodyParsers.Default = instanceOf[BodyParsers.Default]
  val fakeRequest                 = FakeRequest()

  class Harness(action: IdentifierAction) {
    def onPageLoad() = action(request => Results.Ok)
  }

  "AuthenticatedIdentifierAction" when {

    "the user hasn't logged in" must {
      "redirect the user to log in " in {
        val result: Future[Result] = handleAuthError(MissingBearerToken())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must beTheLoginPage
      }
    }

    "the user's session has expired" must {
      "redirect the user to log in " in {
        val result: Future[Result] = handleAuthError(BearerTokenExpired())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must beTheLoginPage
      }
    }

    "the user's credentials are invalid" must {
      "redirect the user to log in " in {
        val result: Future[Result] = handleAuthError(InvalidBearerToken())

        status(result) mustBe SEE_OTHER
        redirectLocation(result).get must beTheLoginPage
      }
    }
  }

  private def beTheLoginPage =
    startWith(appConfig.loginUrl)

  private def handleAuthError(exc: AuthorisationException): Future[Result] = {
    val authAction = new AuthenticatedIdentifierAction(new FakeFailingAuthConnector(exc), appConfig, parser)
    val controller = new Harness(authAction)
    controller.onPageLoad()(fakeRequest)
  }

}

class FakeFailingAuthConnector(exceptionToReturn: Throwable) extends AuthConnector {

  override def authorise[A](predicate: Predicate, retrieval: Retrieval[A])(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[A] =
    Future.failed(exceptionToReturn)

}
