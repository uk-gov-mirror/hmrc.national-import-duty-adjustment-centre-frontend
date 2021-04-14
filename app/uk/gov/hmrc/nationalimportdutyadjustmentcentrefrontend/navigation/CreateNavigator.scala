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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.makeclaim
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Answers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, ReclaimDutyType, RepayTo}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._

@Singleton
class CreateNavigator @Inject() ()
    extends Navigator[CreateAnswers] with CreateAnswerConditions with CreateHasAnsweredConditions {

  override protected val pageOrder: Seq[P] = Seq(
    P(FirstPage, makeclaim.routes.RepresentationTypeController.onPageLoad, never, always),
    P(
      RepresentationTypePage,
      makeclaim.routes.RepresentationTypeController.onPageLoad,
      always,
      representationTypeAnswered
    ),
    P(ClaimTypePage, makeclaim.routes.ClaimTypeController.onPageLoad, always, claimTypeAnswered),
    P(EntryDetailsPage, makeclaim.routes.EntryDetailsController.onPageLoad, always, entryDetailsAnswered),
    P(ItemNumbersPage, makeclaim.routes.ItemNumbersController.onPageLoad, always, itemNumbersAnswered),
    P(ClaimReasonPage, makeclaim.routes.ClaimReasonController.onPageLoad, always, claimReasonAnswered),
    P(ReclaimDutyTypePage, makeclaim.routes.ReclaimDutyTypeController.onPageLoad, always, reclaimDutyTypeAnswered),
    P(
      CustomsDutyRepaymentPage,
      makeclaim.routes.DutyRepaymentController.onPageLoadCustomsDuty,
      hasDutyType(Customs),
      dutyPaymentAnswered(Customs)
    ),
    P(
      ImportVatRepaymentPage,
      makeclaim.routes.DutyRepaymentController.onPageLoadImportVat,
      hasDutyType(Vat),
      dutyPaymentAnswered(Vat)
    ),
    P(
      OtherDutyRepaymentPage,
      makeclaim.routes.DutyRepaymentController.onPageLoadOtherDuty,
      hasDutyType(Other),
      dutyPaymentAnswered(Other)
    ),
    P(
      ReturnAmountSummaryPage,
      makeclaim.routes.ReturnAmountSummaryController.onPageLoad,
      always,
      returnSummaryAnswered
    ),
    P(RequiredDocumentsPage, makeclaim.routes.RequiredDocumentsController.onPageLoad, always, always),
    P(UploadPage, makeclaim.routes.UploadFormController.onPageLoad, always, uploadPageAnswered),
    P(ContactDetailsPage, makeclaim.routes.ContactDetailsController.onPageLoad, always, contactDetailsAnswered),
    P(BusinessNamePage, makeclaim.routes.BusinessNameController.onPageLoad, always, businessNameAnswered),
    P(AddressPage, makeclaim.routes.AddressController.onPageLoad, always, claimantAnswered),
    P(RepayToPage, makeclaim.routes.RepayToController.onPageLoad, isRepresentative, repayToAnswered),
    P(BankDetailsPage, makeclaim.routes.BankDetailsController.onPageLoad, always, bankDetailsAnswered),
    P(
      ImporterEoriNumberPage,
      makeclaim.routes.ImporterEoriNumberController.onPageLoad,
      enterImporterEori,
      importerEoriNumberAnswered
    ),
    P(
      ImporterBusinessNamePage,
      makeclaim.routes.ImporterBusinessNameController.onPageLoad,
      isRepresentative,
      importerBusinessNameAnswered
    ),
    P(
      ImporterContactDetailsPage,
      makeclaim.routes.ImporterDetailsController.onPageLoad,
      isRepresentative,
      importerContactDetailsAnswered
    ),
    P(CheckYourAnswersPage, makeclaim.routes.CheckYourAnswersController.onPageLoad, always, never),
    P(ConfirmationPage, makeclaim.routes.ConfirmationController.onPageLoad, always, never)
  )

  override protected def checkYourAnswersPage: Call = makeclaim.routes.CheckYourAnswersController.onPageLoad

  override protected def pageFor: String => Option[Page] = (pageName: String) => {
    pageName match {
      case CreatePageNames.representationType   => Some(RepresentationTypePage)
      case CreatePageNames.claimType            => Some(ClaimTypePage)
      case CreatePageNames.entryDetails         => Some(EntryDetailsPage)
      case CreatePageNames.itemNumbers          => Some(ItemNumbersPage)
      case CreatePageNames.claimReason          => Some(ClaimReasonPage)
      case CreatePageNames.dutyTypes            => Some(ReclaimDutyTypePage)
      case CreatePageNames.dutyCustoms          => Some(CustomsDutyRepaymentPage)
      case CreatePageNames.dutyVAT              => Some(ImportVatRepaymentPage)
      case CreatePageNames.dutyOther            => Some(OtherDutyRepaymentPage)
      case CreatePageNames.dutySummary          => Some(ReturnAmountSummaryPage)
      case CreatePageNames.uploadSummary        => Some(UploadPage)
      case CreatePageNames.contactDetails       => Some(ContactDetailsPage)
      case CreatePageNames.businessName         => Some(BusinessNamePage)
      case CreatePageNames.contactAddress       => Some(AddressPage)
      case CreatePageNames.importerEori         => Some(ImporterEoriNumberPage)
      case CreatePageNames.importerBusinessName => Some(ImporterBusinessNamePage)
      case CreatePageNames.importerDetails      => Some(ImporterContactDetailsPage)
      case CreatePageNames.repayTo              => Some(RepayToPage)
      case CreatePageNames.bankDetails          => Some(BankDetailsPage)
      case CreatePageNames.checkYourAnswers     => Some(CheckYourAnswersPage)
      case _                                    => None
    }
  }

}

object CreatePageNames {
  val representationType   = "representation-type"
  val claimType            = "claim-type"
  val entryDetails         = "entry-details"
  val itemNumbers          = "item-numbers"
  val claimReason          = "claim-reason"
  val dutyTypes            = "duty-types"
  val dutyCustoms          = "duty-customs"
  val dutyVAT              = "duty-vat"
  val dutyOther            = "duty-other"
  val dutySummary          = "duty-summary"
  val uploadSummary        = "uploaded-files"
  val contactDetails       = "contact-details"
  val businessName         = "enter-business-name"
  val contactAddress       = "contact-address"
  val importerEori         = "importer-eori"
  val importerBusinessName = "enter-importers-name"
  val importerDetails      = "importer-details"
  val repayTo              = "repay-to"
  val bankDetails          = "bank-details"
  val checkYourAnswers     = "check-answers"
}

protected trait CreateAnswerConditions {

  protected val always: Answers => Boolean = (_: Answers) => true

  protected val hasDutyType: ReclaimDutyType => CreateAnswers => Boolean = (dutyType: ReclaimDutyType) =>
    _.reclaimDutyTypes.contains(dutyType)

  protected val hasNoUploads: CreateAnswers => Boolean = _.uploads.isEmpty

  protected val hasUploads: CreateAnswers => Boolean = _.uploads.nonEmpty

  protected val isRepresentative: CreateAnswers => Boolean = _.isRepresentative

  protected val enterImporterEori: CreateAnswers => Boolean = (answers: CreateAnswers) =>
    isRepresentative(answers) && answers.repayTo.contains(RepayTo.Importer)

}

protected trait CreateHasAnsweredConditions {

  protected val never: Answers => Boolean = (_: Answers) => false

  protected val representationTypeAnswered: CreateAnswers => Boolean = _.representationType.nonEmpty
  protected val claimTypeAnswered: CreateAnswers => Boolean          = _.claimType.nonEmpty
  protected val entryDetailsAnswered: CreateAnswers => Boolean       = _.entryDetails.nonEmpty
  protected val itemNumbersAnswered: CreateAnswers => Boolean        = _.itemNumbers.nonEmpty
  protected val claimReasonAnswered: CreateAnswers => Boolean        = _.claimReason.nonEmpty
  protected val reclaimDutyTypeAnswered: CreateAnswers => Boolean    = _.reclaimDutyTypes.nonEmpty

  protected val dutyPaymentAnswered: ReclaimDutyType => CreateAnswers => Boolean = (dutyType: ReclaimDutyType) =>
    _.reclaimDutyPayments.contains(dutyType)

  protected val uploadPageAnswered: CreateAnswers => Boolean = (answers: CreateAnswers) => answers.uploads.nonEmpty

  protected val contactDetailsAnswered: CreateAnswers => Boolean = _.contactDetails.nonEmpty
  protected val businessNameAnswered: CreateAnswers => Boolean   = _.businessName.nonEmpty
  protected val claimantAnswered: CreateAnswers => Boolean       = _.claimantAddress.nonEmpty

  protected val importerEoriNumberAnswered: CreateAnswers => Boolean = (answers: CreateAnswers) =>
    answers.isRepresentative && answers.repayTo.contains(RepayTo.Importer) && answers.importerEori.nonEmpty

  protected val importerBusinessNameAnswered: CreateAnswers => Boolean = (answers: CreateAnswers) =>
    answers.isRepresentative && answers.importerBusinessName.nonEmpty

  protected val importerContactDetailsAnswered: CreateAnswers => Boolean = (answers: CreateAnswers) =>
    answers.isRepresentative && answers.importerContactDetails.nonEmpty

  protected val repayToAnswered: CreateAnswers => Boolean = (answers: CreateAnswers) =>
    answers.isRepresentative && answers.repayTo.nonEmpty

  protected val bankDetailsAnswered: CreateAnswers => Boolean = _.bankDetails.nonEmpty

  private val dutyPages: Set[String] = Set(
    CreatePageNames.dutyTypes,
    CreatePageNames.dutyCustoms,
    CreatePageNames.dutyVAT,
    CreatePageNames.dutyOther,
    CreatePageNames.dutySummary
  )

  protected val returnSummaryAnswered: CreateAnswers => Boolean = (answers: CreateAnswers) =>
    !answers.changePage.exists(page => dutyPages.contains(page))

}
