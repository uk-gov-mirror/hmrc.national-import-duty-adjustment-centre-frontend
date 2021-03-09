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
import org.mockito.Mockito.{reset, verify, verifyNoMoreInteractions, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.mockito.MockitoSugar.mock
import uk.gov.hmrc.http.{HeaderCarrier, NotFoundException}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{BarsTestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.BARSConnector

import scala.concurrent.{ExecutionContext, Future}

class BankAccountReputationServiceSpec extends UnitSpec with BeforeAndAfterEach with BarsTestData {

  implicit val ec: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  implicit val hc: HeaderCarrier = mock[HeaderCarrier]

  private val connector = mock[BARSConnector]

  private def service = new BankAccountReputationService(connector)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    when(connector.sortcodeMetadata(any())(any())).thenReturn(Future.successful(Some(validMetaDataResponse)))
    when(connector.assessBusinessBankDetails(any())(any())).thenReturn(Future.successful(validAssessResponse))
  }

  override protected def afterEach(): Unit = {
    reset(connector)
    super.afterEach()
  }

  "BankAccountReputationService" should {

    "return valid response when BARs returns valid response" in {
      service.validate(bankDetailsAnswer).futureValue.isValid mustBe true

      verify(connector).sortcodeMetadata(any())(any())
      verify(connector).assessBusinessBankDetails(any())(any())
    }

    "return invalid response when BARs returns invalid response" in {
      when(connector.assessBusinessBankDetails(any())(any())).thenReturn(
        Future.successful(invalidAccountNumberAssessResponse)
      )

      service.validate(bankDetailsAnswer).futureValue.isValid mustBe false

      verify(connector).sortcodeMetadata(any())(any())
      verify(connector).assessBusinessBankDetails(any())(any())
    }

    "not call 'assess' if metadata invalid" in {
      when(connector.sortcodeMetadata(any())(any())).thenReturn(Future.successful(Some(noBacsMetaDataResponse)))

      service.validate(bankDetailsAnswer).futureValue.isValid mustBe false

      verify(connector).sortcodeMetadata(any())(any())
      verifyNoMoreInteractions(connector)
    }

    "return 'Bars Not Found' result when BARS response with 404" in {
      when(connector.sortcodeMetadata(any())(any())).thenReturn(Future.successful(None))

      val result = service.validate(bankDetailsAnswer).futureValue

      result.isValid mustBe false
      result.sortcodeExists mustBe false

      verify(connector).sortcodeMetadata(any())(any())
      verifyNoMoreInteractions(connector)

    }

  }
}
