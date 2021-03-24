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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{EoriNumber, FileTransferResult}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadedFile

case class CreateClaimAudit(
  success: Boolean,
  caseReferenceNumber: Option[String],
  contactDetails: ContactDetails,
  claimantAddress: Address,
  representationType: RepresentationType,
  claimType: ClaimType,
  claimReason: ClaimReason,
  reclaimDutyPayments: Map[String, DutyPaid],
  bankDetails: BankDetails,
  importerContactDetails: Option[ImporterContactDetails],
  repayTo: Option[RepayTo],
  entryDetails: EntryDetails,
  itemNumbers: ItemNumbers,
  uploads: Seq[UploadedFile],
  fileTransferResults: Seq[FileTransferResult],
  claimantEori: EoriNumber,
  importerEori: Option[EoriNumber]
)

object CreateClaimAudit {

  implicit val claimWrites: Writes[CreateClaimAudit] = Json.writes[CreateClaimAudit]

  def apply(success: Boolean, claim: Claim, claimResponse: CreateClaimResponse): CreateClaimAudit =
    new CreateClaimAudit(
      success,
      claimResponse.result.map(result => result.caseReference),
      claim.contactDetails,
      claim.claimantAddress,
      claim.representationType,
      claim.claimType,
      claim.claimReason,
      claim.reclaimDutyPayments.map(kv => (dutyTypeToString(kv._1), kv._2)),
      claim.bankDetails,
      claim.importerBeingRepresentedDetails.map(details => details.contactDetails),
      claim.importerBeingRepresentedDetails.map(details => details.repayTo),
      claim.entryDetails,
      claim.itemNumbers,
      claim.uploads,
      claimResponse.result.map(result => result.fileTransferResults).getOrElse(Seq.empty),
      claim.claimantEori,
      claim.importerBeingRepresentedDetails.flatMap(details => details.eoriNumber)
    )

  def dutyTypeToString: ReclaimDutyType => String = {
    case ReclaimDutyType.Customs => "Customs"
    case ReclaimDutyType.Vat     => "Vat"
    case ReclaimDutyType.Other   => "Other"
  }

}
