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

import java.time.LocalDate
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ClaimType.AntiDumping
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{BankDetails, BusinessName, Claim, ClaimReason, ContactDetails, DutyPaid, EntryDetails, ImporterBeingRepresentedDetails, ImporterContactDetails, ItemNumbers, RepayTo, RepresentationType, Address => UkAddress}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{EoriNumber, create}

class EISCreateCaseRequestSpec extends UnitSpec with TestData {

  "EISCreateCaseRequest" should {

    "create Content for Representative Claim" in {

      EISCreateCaseRequest.Content(claimByRepresentative) must be(contentForRepresentativeClaim)
    }

    "create Content for Importer Claim" in {

      EISCreateCaseRequest.Content(claimByImporter) must be(contentForImporterClaim)
    }
  }

  val claimByRepresentative: Claim = create.Claim(
    claimantEori = claimantEori,
    contactDetails = ContactDetails("Adam", "Smith", "adam@smith.com", Some("01234567890")),
    businessName = BusinessName("TODO"),
    claimantAddress = UkAddress("Address Line 1", Some("Address Line 2"), "City", "PO12CD"),
    representationType = RepresentationType.Representative,
    claimType = AntiDumping,
    claimReason = ClaimReason("A reason for the claim"),
    uploads = Seq.empty,
    reclaimDutyPayments =
      Map(Customs -> DutyPaid("100", "80"), Vat -> DutyPaid("200.10", "175"), Other -> DutyPaid("10", "5.50")),
    importerBeingRepresentedDetails = Some(
      ImporterBeingRepresentedDetails(
        repayTo = RepayTo.Representative,
        eoriNumber = Some(EoriNumber("GB098765432123")),
        contactDetails =
          ImporterContactDetails(
            "Importer Address Line 1",
            Some("Importer Address Line 2"),
            "Importer City",
            "IM12CD"
          )
      )
    ),
    bankDetails = BankDetails("account name", "001122", "12345678"),
    entryDetails = EntryDetails("012", "123456Q", LocalDate.of(2020, 12, 31)),
    itemNumbers = ItemNumbers("1, 2, 5-10"),
    submissionDate = LocalDate.of(2021, 1, 31)
  )

  val contentForRepresentativeClaim: EISCreateCaseRequest.Content = EISCreateCaseRequest.Content(
    RepresentationType = "Representative of importer",
    ClaimType = "Anti-Dumping",
    ImporterDetails = ImporterDetails(
      Some("GB098765432123"),
      "TODO",
      ImporterAddress(
        "Importer Address Line 1",
        Some("Importer Address Line 2"),
        "Importer City",
        "IM12CD",
        "GB",
        None,
        None
      )
    ),
    AgentDetails = Some(
      AgentDetails(
        Some(claimantEori.number),
        "TODO",
        AgentAddress(
          "Address Line 1",
          Some("Address Line 2"),
          "City",
          "PO12CD",
          "GB",
          Some("01234567890"),
          "adam@smith.com"
        )
      )
    ),
    EntryProcessingUnit = "012",
    EntryNumber = "123456Q",
    EntryDate = "20201231",
    DutyDetails =
      Seq(DutyDetail("01", "100.00", "80.00"), DutyDetail("02", "200.10", "175.00"), DutyDetail("03", "10.00", "5.50")),
    PayTo = "Representative of importer",
    PaymentDetails = Some(PaymentDetails("account name", "12345678", "001122")),
    ItemNumber = "1, 2, 5-10",
    ClaimReason = "A reason for the claim",
    FirstName = "Adam",
    LastName = "Smith",
    SubmissionDate = "20210131"
  )

  val claimByImporter: Claim = create.Claim(
    claimantEori = claimantEori,
    contactDetails = ContactDetails("Adam", "Smith", "adam@smith.com", Some("01234567890")),
    businessName = BusinessName("Acme Import Co Ltd"),
    claimantAddress = UkAddress("Address Line 1", Some("Address Line 2"), "City", "PO12CD"),
    representationType = RepresentationType.Importer,
    claimType = AntiDumping,
    claimReason = ClaimReason("A reason for the claim"),
    uploads = Seq.empty,
    reclaimDutyPayments =
      Map(Customs -> DutyPaid("100", "80"), Vat -> DutyPaid("200.10", "175"), Other -> DutyPaid("10", "5.50")),
    importerBeingRepresentedDetails = None,
    bankDetails = BankDetails("account name", "001122", "12345678"),
    entryDetails = EntryDetails("012", "123456Q", LocalDate.of(2020, 12, 31)),
    itemNumbers = ItemNumbers("1, 2, 5-10"),
    submissionDate = LocalDate.of(2021, 1, 31)
  )

  val contentForImporterClaim: EISCreateCaseRequest.Content = EISCreateCaseRequest.Content(
    RepresentationType = "Importer",
    ClaimType = "Anti-Dumping",
    ImporterDetails = ImporterDetails(
      Some(claimantEori.number),
      "Acme Import Co Ltd",
      ImporterAddress(
        "Address Line 1",
        Some("Address Line 2"),
        "City",
        "PO12CD",
        "GB",
        Some("01234567890"),
        Some("adam@smith.com")
      )
    ),
    AgentDetails = None,
    EntryProcessingUnit = "012",
    EntryNumber = "123456Q",
    EntryDate = "20201231",
    DutyDetails =
      Seq(DutyDetail("01", "100.00", "80.00"), DutyDetail("02", "200.10", "175.00"), DutyDetail("03", "10.00", "5.50")),
    PayTo = "Importer",
    PaymentDetails = Some(PaymentDetails("account name", "12345678", "001122")),
    ItemNumber = "1, 2, 5-10",
    ClaimReason = "A reason for the claim",
    FirstName = "Adam",
    LastName = "Smith",
    SubmissionDate = "20210131"
  )

}
