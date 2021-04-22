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
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures.convertScalaFuture
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.MessagesApi
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{BarsTestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.AddressLookupConnector
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup.AddressLookupOnRamp
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

import scala.concurrent.{ExecutionContext, Future}

class AddressLookupServiceSpec
    extends UnitSpec with MockitoSugar with Injector with BeforeAndAfterEach with BarsTestData {

  val realMessagesApi: MessagesApi = instanceOf[MessagesApi]
  val appConfig: AppConfig         = instanceOf[AppConfig]

  implicit val ec: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  implicit val hc: HeaderCarrier = mock[HeaderCarrier]

  private val connector = mock[AddressLookupConnector]

  private def service = new AddressLookupService(connector, realMessagesApi, appConfig)

  override protected def beforeEach(): Unit = {
    super.beforeEach()
    when(connector.getAddress(any())(any(), any())).thenReturn(Future.successful(addressLookupConfirmation))
    when(connector.initialiseJourney(any())(any(), any())).thenReturn(
      Future.successful(new AddressLookupOnRamp("callBackUrl"))
    )

  }

  override protected def afterEach(): Unit = {
    reset(connector)
    super.afterEach()
  }

  "AddressLookupService" should {

    "return an valid address when Address Lookup returns a valid response" in {
      service.retrieveAddress("byid").futureValue mustBe addressLookupConfirmation
      verify(connector).getAddress(any())(any(), any())
    }

    "initialises the journey when" in {
      service.initialiseJourney("callBackUrl", "page.key")
      verify(connector).initialiseJourney(any())(any(), any())
    }

  }
}
