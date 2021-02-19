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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base

import java.time.{LocalDate, LocalDateTime, ZonedDateTime}

import reactivemongo.bson.BSONObjectID
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.Reference
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ClaimType.AntiDumping
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UpscanNotification.Quarantine
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{RepayTo, RepresentationType, _}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.repositories.UploadDetails

trait TestData {

  val fixedDate: LocalDate         = LocalDate.now()
  val fixedDateTime: LocalDateTime = LocalDateTime.now()

  // UserAnswers
  val emptyAnswers: UserAnswers = UserAnswers()

  val representationTypeAnswer: RepresentationType = RepresentationType.Representative

  val claimTypeAnswer: ClaimType = AntiDumping

  val uploadAnswer: UploadedFile =
    UploadedFile("upscanRef1", "/url", ZonedDateTime.now(), "checksum", "filename", "mime/type")

  val uploadAnswer2: UploadedFile =
    UploadedFile("upscanRef2", "/url2", ZonedDateTime.now(), "checksum2", "filename2", "mime/type2")

  val reclaimDutyTypesAnswer: Set[ReclaimDutyType] = Set(Customs, Vat, Other)

  val customsDutyRepaymentAnswer: DutyPaid = DutyPaid("100", "9.99")
  val importVatRepaymentAnswer: DutyPaid   = DutyPaid("100", "9.99")
  val otherDutyRepaymentAnswer: DutyPaid   = DutyPaid("100", "9.99")

  val reclaimDutyPayments = Map(
    Customs.toString -> customsDutyRepaymentAnswer,
    Vat.toString     -> importVatRepaymentAnswer,
    Other.toString   -> otherDutyRepaymentAnswer
  )

  val bankDetailsAnswer: BankDetails = BankDetails("account name", "001100", "12345678")

  val repayToAnswer = RepayTo.Importer

  val claimReasonAnswer: ClaimReason = ClaimReason("some valid reason")

  val contactDetailsAnswer: ContactDetails = ContactDetails("Jane", "Doe", "jane@example.com", "01234567890")

  val addressAnswer: Address = Address("Name", "Line 1", Some("Line 2"), "City", "PCode")

  val entryDetailsAnswer: EntryDetails = EntryDetails("010", "123456Q", fixedDate)

  val itemNumbersAnswer: ItemNumbers = ItemNumbers("1,2,3,4")

  val importerHasEoriAnswer = true

  val importerEoriNumberAnswer = EoriNumber("GB232454456746")

  val importerContactDetailsAnswer = ImporterContactDetails(
    "Importer Name",
    "Importer Line 1",
    Some("Importer Line 2"),
    "Importer City",
    "PCode",
    "importer@example.com",
    "01234567890"
  )

  val completeAnswers: UserAnswers = UserAnswers(
    representationType = Some(representationTypeAnswer),
    claimType = Some(claimTypeAnswer),
    claimReason = Some(claimReasonAnswer),
    contactDetails = Some(contactDetailsAnswer),
    claimantAddress = Some(addressAnswer),
    uploads = Seq(uploadAnswer),
    reclaimDutyTypes = reclaimDutyTypesAnswer,
    reclaimDutyPayments = reclaimDutyPayments,
    repayTo = Some(repayToAnswer),
    bankDetails = Some(bankDetailsAnswer),
    entryDetails = Some(entryDetailsAnswer),
    itemNumbers = Some(itemNumbersAnswer),
    importerHasEori = Some(importerHasEoriAnswer),
    importerEori = Some(importerEoriNumberAnswer),
    importerContactDetails = Some(importerContactDetailsAnswer)
  )

  // Upscan
  val uploadId: UploadId   = UploadId.generate
  val journeyId: JourneyId = JourneyId.generate

  val upscanInitiateResponse: UpscanInitiateResponse =
    UpscanInitiateResponse(UpscanFileReference("file-ref"), "post-target", Map("field-hidden" -> "value-hidden"))

  def uploadResult(status: UploadStatus): UploadDetails =
    UploadDetails(BSONObjectID.generate(), uploadId, journeyId, Reference("reference"), status, fixedDateTime)

  val uploadInProgress: UploadStatus = InProgress
  val uploadFailed: UploadStatus     = Failed(Quarantine, "bad file")

  val uploadFileSuccess: UploadStatus =
    UploadedFile("upscanRef1", "downloadUrl", ZonedDateTime.now(), "checksum", "fileName", "fileMimeType")

}
