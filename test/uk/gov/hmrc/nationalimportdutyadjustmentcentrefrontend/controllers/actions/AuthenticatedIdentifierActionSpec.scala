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

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, spy, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.Configuration
import play.api.mvc.{AnyContentAsEmpty, BodyParsers, Result, Results}
import play.api.test.FakeRequest
import play.api.test.Helpers.{redirectLocation, _}
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{~, Retrieval}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.frontend.filters.SessionTimeoutFilterConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuthenticatedIdentifierActionSpec extends UnitSpec with MockitoSugar with Injector with BeforeAndAfterEach {

  val config: Configuration                            = instanceOf[Configuration]
  val servicesConfig: ServicesConfig                   = instanceOf[ServicesConfig]
  val sessionTimeoutConfig: SessionTimeoutFilterConfig = instanceOf[SessionTimeoutFilterConfig]

  val mockConfig: Configuration = spy(config)

  val parser: BodyParsers.Default                      = instanceOf[BodyParsers.Default]
  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  val authConnector: AuthConnector = mock[AuthConnector]

  val usersEORI: String = "GB1234567890"

  def appConfig = new AppConfig(mockConfig, servicesConfig, sessionTimeoutConfig)

  val enrolmentsWithoutEORI: Enrolments = Enrolments(
    Set(Enrolment(key = "IR-SA", identifiers = Seq(EnrolmentIdentifier("UTR", "123")), state = "Activated"))
  )

  val enrolmentsWithEORI: Enrolments = Enrolments(
    Set(
      Enrolment(
        key = "HMRC-CTS-ORG",
        identifiers = Seq(EnrolmentIdentifier("EORINumber", usersEORI)),
        state = "Activated"
      )
    )
  )

  override protected def beforeEach(): Unit =
    super.beforeEach()

  override protected def afterEach(): Unit = {
    reset(authConnector)
    super.afterEach()
  }

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

    "user does not have enrolment with EORI" must {
      "redirect to unauthorised page" in {
        val result: Future[Result] = handleAuthWithEnrolments(enrolmentsWithoutEORI)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)
      }
    }

    "user's EORI not on allow list" must {
      "redirect to service unavailable page" in {
        when(mockConfig.get[Boolean]("eori.allowList.enabled")).thenReturn(true)

        val result: Future[Result] = handleAuthWithEnrolments(enrolmentsWithEORI)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.ServiceUnavailableController.onPageLoad().url)
      }
    }

    "user's EORI is on allow list" must {
      "allow through" in {
        when(mockConfig.get[Boolean]("eori.allowList.enabled")).thenReturn(true)
        when(mockConfig.get[Seq[String]]("eori.allowList.eoris")).thenReturn(Seq(usersEORI))

        val result: Future[Result] = handleAuthWithEnrolments(enrolmentsWithEORI)

        status(result) mustBe OK
      }
    }
  }

  private def beTheLoginPage =
    startWith(appConfig.loginUrl)

  private def handleAuthError(exc: AuthorisationException): Future[Result] = {

    when(authConnector.authorise[Enrolments](any(), any())(any(), any()))
      .thenReturn(Future.failed(exc))

    val authAction = new AuthenticatedIdentifierAction(authConnector, appConfig, parser)
    val controller = new Harness(authAction)
    controller.onPageLoad()(fakeRequest)
  }

  private def handleAuthWithEnrolments(enrolments: Enrolments): Future[Result] = {

    when(authConnector.authorise(any(), any[Retrieval[~[Option[String], Enrolments]]])(any(), any())).thenReturn(
      Future.successful(new ~[Option[String], Enrolments](Some("identifier"), enrolments))
    )

    val authAction = new AuthenticatedIdentifierAction(authConnector, appConfig, parser)
    val controller = new Harness(authAction)
    controller.onPageLoad()(fakeRequest)
  }

}
