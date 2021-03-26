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

import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.http.{HeaderCarrier, RequestId}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.NIDACConnector
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendClaimAudit
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.CreateClaimAudit
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
    when(nidacConnector.amendClaim(any(), any())(any())).thenReturn(Future.successful(validAmendClaimResponse))
  }

  override protected def afterEach(): Unit = {
    reset(nidacConnector, auditConnector)
    super.afterEach()
  }

  "ClaimService" should {

    "return valid response when claim is submitted succesfully" in {
      service.submitClaim(claim).futureValue.correlationId mustBe "123456"
    }

    "audit Create Claim when claim is submitted succesfully" in {

      service.submitClaim(claim)

      val carrierCaptor   = ArgumentCaptor.forClass(classOf[HeaderCarrier])
      val executionCaptor = ArgumentCaptor.forClass(classOf[ExecutionContext])

      val auditCaptor = ArgumentCaptor.forClass(classOf[CreateClaimAudit])

      (verify(auditConnector) sendExplicitAudit (any(), auditCaptor.capture()))(
        carrierCaptor.capture(),
        executionCaptor.capture(),
        any()
      )

      val audit = auditCaptor.getValue.asInstanceOf[CreateClaimAudit]
      audit mustBe createClaimAudit

    }

    "return valid response when claim is amended succesfully" in {
      service.amendClaim(amendClaim).futureValue.correlationId mustBe "123456"
    }

    "audit Amend Claim when claim is amended succesfully" in {

      service.amendClaim(amendClaim)

      val headerCarrier    = ArgumentCaptor.forClass(classOf[HeaderCarrier])
      val executionCarrier = ArgumentCaptor.forClass(classOf[ExecutionContext])

      val amendAuditCaptor = ArgumentCaptor.forClass(classOf[AmendClaimAudit])

      (verify(auditConnector) sendExplicitAudit (any(), amendAuditCaptor.capture()))(
        headerCarrier.capture(),
        executionCarrier.capture(),
        any()
      )

      val audit = amendAuditCaptor.getValue.asInstanceOf[AmendClaimAudit]
      audit mustBe amendClaimAudit

    }

  }
}
