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

import java.time.LocalDateTime

import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatestplus.mockito.MockitoSugar
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ClaimType.Airworthiness
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UserAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.{DataRequest, IdentifierRequest}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.SessionRepository

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataRetrievalActionSpec extends UnitSpec with MockitoSugar with ScalaFutures {

  class Harness(sessionRepository: SessionRepository) extends DataRetrievalActionImpl(sessionRepository) {
    def callTransform[A](request: IdentifierRequest[A]): Future[DataRequest[A]] = transform(request)
  }

  val fakeRequest: FakeRequest[AnyContentAsEmpty.type] = FakeRequest()

  "Data Retrieval Action" when {

    "there is no data in the cache" must {

      "return a new UserAnswers" in {
        val sessionRepository = mock[SessionRepository]
        when(sessionRepository.get("unknown-id")) thenReturn Future(None)
        val action = new Harness(sessionRepository)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "unknown-id"))

        whenReady(futureResult) { result =>
          result.userAnswers.id mustBe "unknown-id"
        }
      }
    }

    "there is data in the cache" must {

      "return cached UserAnswers and add it to the request" in {

        val answers =
          UserAnswers("id", claimType = Some(Airworthiness), lastUpdated = LocalDateTime.now().minusMinutes(5))
        val sessionRepository = mock[SessionRepository]
        when(sessionRepository.get("id")) thenReturn Future(Some(answers))
        val action = new Harness(sessionRepository)

        val futureResult = action.callTransform(IdentifierRequest(fakeRequest, "id"))

        whenReady(futureResult) { result =>
          result.userAnswers mustBe answers
        }
      }
    }
  }
}
