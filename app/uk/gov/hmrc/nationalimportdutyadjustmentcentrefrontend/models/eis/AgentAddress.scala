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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.eis

import play.api.libs.json.{Json, OFormat}

case class AgentAddress(
  AddressLine1: String,
  AddressLine2: Option[String],
  City: String,
  PostalCode: String,
  CountryCode: String,
  TelephoneNumber: Option[String],
  EmailAddress: String
)

object AgentAddress {
  implicit val format: OFormat[AgentAddress] = Json.format[AgentAddress]
}
