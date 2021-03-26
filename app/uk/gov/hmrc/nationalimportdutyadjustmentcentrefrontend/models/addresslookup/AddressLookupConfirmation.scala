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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup
import play.api.libs.json.{Json, OFormat}

case class AddressLookupConfirmation(auditRef: String, id: String, address: AddressLookupAddress)

object AddressLookupConfirmation {
  implicit val format: OFormat[AddressLookupConfirmation] = Json.format[AddressLookupConfirmation]

  def apply(auditRef: String, id: String, address: AddressLookupAddress): AddressLookupConfirmation =
    new AddressLookupConfirmation(auditRef, id, address)

}
/*
{
    "auditRef" : "bed4bd24-72da-42a7-9338-f43431b7ed72",
    "id" : "GB990091234524",
    "address" : {
        "lines" : [ "10 Other Place", "Some District", "Anytown" ],
        "postcode" : "ZZ1 1ZZ",
        "country" : {
            "code" : "GB",
            "name" : "United Kingdom"
        }
    }
}
 */