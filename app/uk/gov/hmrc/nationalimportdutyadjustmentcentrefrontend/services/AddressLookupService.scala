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

import play.api.i18n.MessagesApi
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.AddressLookupConnector
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup.{
  AddressLookupConfirmation,
  AddressLookupOnRamp,
  AddressLookupRequest
}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddressLookupService @Inject() (
  addressLookupConnector: AddressLookupConnector,
  implicit val messagesApi: MessagesApi,
  implicit val appConfig: AppConfig
) {

  def retrieveAddress(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AddressLookupConfirmation] =
    addressLookupConnector.getAddress(id)

  def initialiseJourney(callBackUrl: String, lookupPageHeadingKey: String)(implicit
    hc: HeaderCarrier,
    ec: ExecutionContext
  ): Future[AddressLookupOnRamp] =
    addressLookupConnector.initialiseJourney(AddressLookupRequest(callBackUrl, lookupPageHeadingKey))

}
