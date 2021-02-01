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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingUserAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadedFile
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{
  ClaimTypePage,
  EntryDetailsPage,
  Page,
  ReclaimDutyTypePage,
  UploadPage
}

case class CreateClaimRequest(
  userId: String,
  claimType: ClaimType,
  uploads: Seq[UploadedFile],
  reclaimDutyTypes: Set[ReclaimDutyType],
  bankDetails: Option[BankDetails],
  entryDetails: EntryDetails
)

object CreateClaimRequest {

  implicit val format: OFormat[CreateClaimRequest] = Json.format[CreateClaimRequest]

  def apply(id: String, userAnswers: UserAnswers): CreateClaimRequest =
    new CreateClaimRequest(
      userId = id,
      claimType = userAnswers.claimType.getOrElse(missing(ClaimTypePage)),
      uploads = userAnswers.uploads.getOrElse(missing(UploadPage)),
      reclaimDutyTypes = userAnswers.reclaimDutyTypes.getOrElse(missing(ReclaimDutyTypePage)),
      bankDetails = userAnswers.bankDetails,
      entryDetails = userAnswers.entryDetails.getOrElse(missing(EntryDetailsPage))
    )

  private def missing(answer: Page) =
    throw new MissingUserAnswersException(s"missing answer - $answer")

}
