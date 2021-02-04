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

import play.api.Logger
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingUserAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadedFile
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._

import scala.util.Try

case class Claim(
  contactDetails: ContactDetails,
  importerAddress: Address,
  claimType: ClaimType,
  uploads: Seq[UploadedFile],
  reclaimDutyPayments: Map[ReclaimDutyType, DutyPaid],
  bankDetails: BankDetails,
  entryDetails: EntryDetails
) {

  def repaymentTotal: BigDecimal = reclaimDutyPayments.values.map(_.dueAmount).sum

}

object Claim {

  def apply(userAnswers: UserAnswers): Claim =
    new Claim(
      contactDetails = userAnswers.contactDetails.getOrElse(missing(ContactDetailsPage)),
      importerAddress = userAnswers.importerAddress.getOrElse(missing(AddressPage)),
      claimType = userAnswers.claimType.getOrElse(missing(ClaimTypePage)),
      uploads = userAnswers.uploads.getOrElse(missing(UploadPage)),
      reclaimDutyPayments = userAnswers.reclaimDutyTypes.getOrElse(missing(ReclaimDutyTypePage)).map(
        dutyType =>
          dutyType -> Try(userAnswers.reclaimDutyPayments(dutyType)).getOrElse(missing(s"DutyPayment $dutyType"))
      ).toMap,
      bankDetails = userAnswers.bankDetails.getOrElse(missing(BankDetailsPage)),
      entryDetails = userAnswers.entryDetails.getOrElse(missing(EntryDetailsPage))
    )

  private def missing(answer: Any) = {
    val message = s"Missing answer - $answer"
    Logger(this.getClass).warn(message)
    throw new MissingUserAnswersException(message)
  }

}
