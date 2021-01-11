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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.{any, anyString}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsFormUrlEncoded, Request}
import play.api.test.FakeRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.{
  DataRetrievalActionImpl,
  FakeIdentifierActions
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UserAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.SessionRepository
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.FakeRequestCSRFSupport.CSRFFakeRequest

import scala.concurrent.{ExecutionContext, Future}

trait ControllerSpec extends UnitSpec with MockitoSugar with FakeIdentifierActions with BeforeAndAfterEach {

  implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  val sessionRepository: SessionRepository = mock[SessionRepository]

  val fakeGetRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("GET", "/")

  val dataRetrievalAction = new DataRetrievalActionImpl(sessionRepository)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    when(sessionRepository.set(any[UserAnswers])).thenReturn(Future.successful(true))
  }

  override protected def afterEach(): Unit = {
    reset(sessionRepository)
    super.afterEach()
  }

  def withEmptyCache(): Unit = withCachedData(None)

  def withCachedData(answers: Option[UserAnswers]): Unit =
    when(sessionRepository.get(anyString())).thenReturn(Future.successful(answers))

  protected def theUpdatedCache: UserAnswers = {
    val captor = ArgumentCaptor.forClass(classOf[UserAnswers])
    verify(sessionRepository).set(captor.capture())
    captor.getValue
  }

  protected def postRequest(data: (String, String)*): Request[AnyContentAsFormUrlEncoded] =
    FakeRequest("POST", "")
      .withFormUrlEncodedBody(data: _*)
      .withCSRFToken

}
