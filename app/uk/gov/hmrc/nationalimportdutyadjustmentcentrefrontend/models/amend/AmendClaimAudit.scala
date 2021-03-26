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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend

import play.api.libs.json.{Json, Writes}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.FileTransferResult
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadedFile

case class AmendClaimAudit(
  success: Boolean,
  caseReferenceNumber: String,
  uploads: Seq[UploadedFile],
  fileTransferResults: Seq[FileTransferResult],
  furtherInformation: FurtherInformation
)

object AmendClaimAudit {

  implicit val claimWrites: Writes[AmendClaimAudit] = Json.writes[AmendClaimAudit]

  def apply(success: Boolean, claim: AmendClaim, claimResponse: AmendClaimResponse): AmendClaimAudit =
    AmendClaimAudit(
      success,
      claim.caseReference.number,
      claim.uploads,
      claimResponse.result.map(result => result.fileTransferResults).getOrElse(Seq.empty),
      claim.furtherInformation
    )

}
