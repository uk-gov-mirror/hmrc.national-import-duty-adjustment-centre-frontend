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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ReclaimDutyType.Vat
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{
  BankDetails,
  Claim,
  ContactDetails,
  EntryDetails,
  Address => UkAddress
}

class EISCreateCaseRequestSpec extends UnitSpec {

  val claim: Claim = Claim(
    contactDetails = ContactDetails("Adam", "Smith", "adam@smith.com", "01234567890"),
    importerAddress = UkAddress("Import Co Ltd", "Address Line 1", Some("Address Line 2"), "City", "PO12CD"),
    claimType = AntiDumping,
    uploads = Seq.empty,
    reclaimDutyTypes = Set(Vat),
    bankDetails = BankDetails("account name", "001122", "12345678"),
    entryDetails = EntryDetails("012", "123456Q", LocalDate.of(2020, 12, 31))
  )

  val content: EISCreateCaseRequest.Content = EISCreateCaseRequest.Content(
    ClaimType = "Anti-Dumping",
    ImporterDetails = ImporterDetails(
      "Import Co Ltd",
      Address("Address Line 1", Some("Address Line 2"), "City", "PO12CD", "GB", "01234567890", "adam@smith.com")
    ),
    EntryProcessingUnit = "012",
    EntryNumber = "123456Q",
    EntryDate = "20201231",
    DutyDetails = Seq(DutyDetail("02", "0", "0")),
    PaymentDetails = Some(PaymentDetails("account name", "12345678", "001122")),
    FirstName = "Adam",
    LastName = "Smith"
  )

  "EISCreateCaseRequest" should {

    "create Content from valid Claim" in {

      EISCreateCaseRequest.Content(claim) must be(content)
    }
  }

}
