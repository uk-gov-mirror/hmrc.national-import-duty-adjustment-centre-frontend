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

import java.time.LocalDate

import play.api.Logger
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadedFile
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{create, _}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._

import scala.util.Try

case class Claim(
  contactDetails: ContactDetails,
  claimantAddress: Address,
  representationType: RepresentationType,
  claimType: ClaimType,
  claimReason: ClaimReason,
  uploads: Seq[UploadedFile],
  reclaimDutyPayments: Map[ReclaimDutyType, DutyPaid],
  importerBeingRepresentedDetails: Option[ImporterBeingRepresentedDetails],
  bankDetails: BankDetails,
  entryDetails: EntryDetails,
  itemNumbers: ItemNumbers,
  submissionDate: LocalDate
) {

  def repaymentTotal: BigDecimal = reclaimDutyPayments.values.map(_.dueAmount).sum

}

object Claim {

  def apply(userAnswers: CreateAnswers): Claim = {
    if (userAnswers.uploads.isEmpty) missing(UploadSummaryPage)
    if (userAnswers.reclaimDutyTypes.isEmpty) missing(ReclaimDutyTypePage)
    new Claim(
      contactDetails = userAnswers.contactDetails.getOrElse(missing(ContactDetailsPage)),
      claimantAddress = userAnswers.claimantAddress.getOrElse(missing(AddressPage)),
      representationType = userAnswers.representationType.getOrElse(missing(ReclaimDutyTypePage)),
      claimType = userAnswers.claimType.getOrElse(missing(ClaimTypePage)),
      claimReason = userAnswers.claimReason.getOrElse(missing(ClaimReasonPage)),
      uploads = userAnswers.uploads,
      reclaimDutyPayments = userAnswers.reclaimDutyTypes.map(
        dutyType =>
          dutyType -> Try(userAnswers.reclaimDutyPayments(dutyType)).getOrElse(missing(s"DutyPayment $dutyType"))
      ).toMap,
      importerBeingRepresentedDetails = importerBeingRepresentedDetails(userAnswers),
      bankDetails = userAnswers.bankDetails.getOrElse(missing(BankDetailsPage)),
      entryDetails = userAnswers.entryDetails.getOrElse(missing(EntryDetailsPage)),
      itemNumbers = userAnswers.itemNumbers.getOrElse(missing(ItemNumbersPage)),
      submissionDate = LocalDate.now()
    )
  }

  private def importerBeingRepresentedDetails(userAnswers: CreateAnswers): Option[ImporterBeingRepresentedDetails] =
    userAnswers.representationType match {
      case Some(RepresentationType.Importer) => None
      case Some(RepresentationType.Representative) =>
        Some(
          create.ImporterBeingRepresentedDetails(
            repayTo = userAnswers.repayTo.getOrElse(missing(RepayToPage)),
            eoriNumber =
              if (userAnswers.importerHasEori.contains(true))
                Some(userAnswers.importerEori.getOrElse(missing(ImporterEoriNumberPage)))
              else None,
            contactDetails = userAnswers.importerContactDetails.getOrElse(missing(ImporterContactDetailsPage))
          )
        )
    }

  private def missing(answer: Any) = {
    val message = s"Missing answer - $answer"
    Logger(this.getClass).warn(message)
    throw new MissingAnswersException(message)
  }

}
