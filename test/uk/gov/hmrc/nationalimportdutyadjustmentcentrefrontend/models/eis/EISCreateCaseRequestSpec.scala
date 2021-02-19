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

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ClaimType.AntiDumping
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{
  BankDetails,
  Claim,
  ClaimReason,
  ContactDetails,
  DutyPaid,
  EntryDetails,
  EoriNumber,
  ItemNumbers,
  RepayTo,
  RepresentationType,
  Address => UkAddress
}

class EISCreateCaseRequestSpec extends UnitSpec {

  val claim: Claim = Claim(
    contactDetails = ContactDetails("Adam", "Smith", "adam@smith.com", "01234567890"),
    importerAddress = UkAddress("Import Co Ltd", "Address Line 1", Some("Address Line 2"), "City", "PO12CD"),
    representationType = RepresentationType.Representative,
    claimType = AntiDumping,
    claimReason = ClaimReason("A reason for the claim"),
    uploads = Seq.empty,
    reclaimDutyPayments =
      Map(Customs -> DutyPaid("100", "80"), Vat -> DutyPaid("200.10", "175"), Other -> DutyPaid("10", "5.50")),
    repayTo = Some(RepayTo.Representative),
    bankDetails = BankDetails("account name", "001122", "12345678"),
    importerHasEoriNumber = Some(true),
    importerEoriNumber = Some(EoriNumber("GB098765432123")),
    entryDetails = EntryDetails("012", "123456Q", LocalDate.of(2020, 12, 31)),
    itemNumbers = ItemNumbers("1, 2, 5-10"),
    submissionDate = LocalDate.of(2021, 1, 31)
  )

  val content: EISCreateCaseRequest.Content = EISCreateCaseRequest.Content(
    RepresentationType = "Representative of importer",
    ClaimType = "Anti-Dumping",
    ImporterDetails = ImporterDetails(
      "Import Co Ltd",
      Address("Address Line 1", Some("Address Line 2"), "City", "PO12CD", "GB", "01234567890", "adam@smith.com"),
      Some("GB098765432123")
    ),
    EntryProcessingUnit = "012",
    EntryNumber = "123456Q",
    EntryDate = "20201231",
    DutyDetails =
      Seq(DutyDetail("01", "100.00", "20.00"), DutyDetail("02", "200.10", "25.10"), DutyDetail("03", "10.00", "4.50")),
    PayTo = "Representative of importer",
    PaymentDetails = Some(PaymentDetails("account name", "12345678", "001122")),
    ItemNumber = "1, 2, 5-10",
    ClaimReason = "A reason for the claim",
    FirstName = "Adam",
    LastName = "Smith",
    SubmissionDate = "20210131"
  )

  "EISCreateCaseRequest" should {

    "create Content from valid Claim" in {

      EISCreateCaseRequest.Content(claim) must be(content)
    }
  }

}
