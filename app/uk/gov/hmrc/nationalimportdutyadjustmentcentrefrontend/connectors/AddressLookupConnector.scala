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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors

import play.api.Logger
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.InitialiseAddressLookupHttpParser.InitialiseAddressLookupReads
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup.{AddressLookupConfirmation, AddressLookupJsonBuilder, AddressLookupOnRampModel}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.Address

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AddressLookupConnector @Inject()(val http: HttpClient,
                                       implicit val config: AppConfig) {

  def initialiseJourney(addressLookupJsonBuilder: AddressLookupJsonBuilder)
                      (implicit hc: HeaderCarrier,ec: ExecutionContext): Future[AddressLookupOnRampModel] = {

    val url = config.addressLookupInitUrl
    Logger.info(s"[AddressLookupConnector][initialiseJourney]: Calling initialiseJourney with URL - $url")
    http.POST[AddressLookupJsonBuilder, AddressLookupOnRampModel](
      url,
      addressLookupJsonBuilder
    )(implicitly, InitialiseAddressLookupReads, hc, ec)
  }

  private[connectors] def getAddressUrl(id: String) = s"${config.addressLookupConfirmedUrl}?id=$id"

  def getAddress(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AddressLookupConfirmation] ={
    Logger.debug(s"[AddressLookupConnector][getAddress]: Calling getAddress with URL - ${getAddressUrl(id)}")
    http.GET[AddressLookupConfirmation](getAddressUrl(id))(implicitly,hc,ec)
  }
}
