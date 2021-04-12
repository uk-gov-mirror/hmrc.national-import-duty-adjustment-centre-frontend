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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.RepresentationType.{
  Importer,
  Representative
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{
  Claim,
  ContactDetails,
  ImporterBeingRepresentedDetails
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{create, EoriNumber}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.ImporterContactDetailsPage

case class ImporterDetails(EORI: Option[String], Name: String, Address: ImporterAddress)

object ImporterDetails {
  implicit val format: OFormat[ImporterDetails] = Json.format[ImporterDetails]

  def forClaim(claim: Claim): ImporterDetails = claim.representationType match {
    case Representative =>
      forRepresentativeApplicant(
        claim.importerBeingRepresentedDetails.getOrElse(throw new MissingAnswersException(ImporterContactDetailsPage))
      )
    case Importer => forImporterApplicant(claim.claimantEori, claim.contactDetails, claim.claimantAddress)
  }

  private def forImporterApplicant(
    claimantEori: EoriNumber,
    contactDetails: ContactDetails,
    address: create.Address
  ): ImporterDetails =
    new ImporterDetails(
      EORI = Some(claimantEori.number),
      Name = "Dave",
      Address = ImporterAddress(
        AddressLine1 = address.addressLine1,
        AddressLine2 = address.addressLine2,
        City = address.city,
        PostalCode = address.postCode,
        CountryCode = "GB",
        EmailAddress = Some(contactDetails.emailAddress),
        TelephoneNumber = contactDetails.telephoneNumber
      )
    )

  def forRepresentativeApplicant(importer: ImporterBeingRepresentedDetails): ImporterDetails =
    new ImporterDetails(
      EORI = importer.eoriNumber.map(_.number),
      Name = importer.contactDetails.name,
      Address = ImporterAddress(
        AddressLine1 = importer.contactDetails.addressLine1,
        AddressLine2 = importer.contactDetails.addressLine2,
        City = importer.contactDetails.city,
        PostalCode = importer.contactDetails.postCode,
        CountryCode = "GB",
        EmailAddress = None,
        TelephoneNumber = None
      )
    )

}
