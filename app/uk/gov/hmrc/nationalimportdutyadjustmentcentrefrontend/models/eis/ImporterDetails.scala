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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.RepresentationType.{Importer, Representative}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingUserAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{
  Claim,
  ContactDetails,
  EoriNumber,
  ImporterContactDetails,
  Address => UkAddress
}

case class ImporterDetails(EORI: Option[String], Name: String, Address: Address)

object ImporterDetails {
  implicit val format: OFormat[ImporterDetails] = Json.format[ImporterDetails]

  def forClaim(claim: Claim): ImporterDetails = claim.representationType match {
    case Representative =>
      forRepresentativeApplicant(
        claim.importerEoriNumber,
        claim.importerContactDetails.getOrElse(throw new MissingUserAnswersException("Missing ImporterContactDetails"))
      )
    case Importer => forImporterApplicant(claim.contactDetails, claim.claimantAddress)
  }

  private def forImporterApplicant(contactDetails: ContactDetails, address: UkAddress): ImporterDetails =
    new ImporterDetails(
      EORI = None, // TODO - capture applicant's EORI
      Name = address.name,
      Address = Address(
        AddressLine1 = address.addressLine1,
        AddressLine2 = address.addressLine2,
        City = address.city,
        PostalCode = address.postCode,
        CountryCode = "GB",
        EmailAddress = contactDetails.emailAddress,
        TelephoneNumber = contactDetails.telephoneNumber
      )
    )

  def forRepresentativeApplicant(
    importerEoriNumber: Option[EoriNumber],
    importer: ImporterContactDetails
  ): ImporterDetails =
    new ImporterDetails(
      EORI = importerEoriNumber.map(_.number),
      Name = importer.name,
      Address = Address(
        AddressLine1 = importer.addressLine1,
        AddressLine2 = importer.addressLine2,
        City = importer.city,
        PostalCode = importer.postCode,
        CountryCode = "GB",
        EmailAddress = importer.emailAddress,
        TelephoneNumber = importer.telephoneNumber
      )
    )

}
