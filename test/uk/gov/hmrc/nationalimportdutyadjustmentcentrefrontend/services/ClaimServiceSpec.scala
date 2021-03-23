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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.logging.RequestId
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.NIDACConnector
import uk.gov.hmrc.play.audit.http.connector.AuditConnector

import scala.concurrent.{ExecutionContext, Future}

class ClaimServiceSpec extends UnitSpec with BeforeAndAfterEach with TestData {

  implicit val ec: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  implicit val hc: HeaderCarrier = HeaderCarrier(requestId = Some(RequestId("123456")))

  private val auditConnector = mock[AuditConnector]
  private val nidacConnector = mock[NIDACConnector]

  private def service = new ClaimService(auditConnector, nidacConnector)

  override protected def beforeEach(): Unit = {
    super.beforeEach()

    when(nidacConnector.submitClaim(any(), any())(any())).thenReturn(Future.successful(validCreateClaimResponse))
  }

  override protected def afterEach(): Unit = {
    reset(nidacConnector, auditConnector)
    super.afterEach()
  }

  "ClaimService" should {

    "return valid response when claim is submitted succesfully" in {
      service.submitClaim(completeAnswers, claim).futureValue.correlationId mustBe "123456"

    }
  }
}
