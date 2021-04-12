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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.Claim
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.RepresentationType.Representative

case class AgentDetails(EORI: Option[String], Name: String, Address: AgentAddress)

object AgentDetails {
  implicit val format: OFormat[AgentDetails] = Json.format[AgentDetails]

  def forClaim(claim: Claim): Option[AgentDetails] = claim.representationType match {
    case Representative =>
      Some(
        AgentDetails(
          EORI = Some(claim.claimantEori.number),
          Name = "Dave",
          Address = AgentAddress(
            AddressLine1 = claim.claimantAddress.addressLine1,
            AddressLine2 = claim.claimantAddress.addressLine2,
            City = claim.claimantAddress.city,
            PostalCode = claim.claimantAddress.postCode,
            CountryCode = "GB",
            EmailAddress = claim.contactDetails.emailAddress,
            TelephoneNumber = claim.contactDetails.telephoneNumber
          )
        )
      )
    case _ => None
  }

}
