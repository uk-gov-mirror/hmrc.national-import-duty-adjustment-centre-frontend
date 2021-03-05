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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.makeclaim
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Answers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, ReclaimDutyType}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._

@Singleton
class CreateNavigator @Inject() () extends Navigator[CreateAnswers] with CreateAnswerConditions {

  override protected val pageOrder: Seq[P] = Seq(
    P(RepresentationTypePage, makeclaim.routes.RepresentationTypeController.onPageLoad, always),
    P(ClaimTypePage, makeclaim.routes.ClaimTypeController.onPageLoad, always),
    P(EntryDetailsPage, makeclaim.routes.EntryDetailsController.onPageLoad, always),
    P(ItemNumbersPage, makeclaim.routes.ItemNumbersController.onPageLoad, always),
    P(ClaimReasonPage, makeclaim.routes.ClaimReasonController.onPageLoad, always),
    P(ReclaimDutyTypePage, makeclaim.routes.ReclaimDutyTypeController.onPageLoad, always),
    P(CustomsDutyRepaymentPage, makeclaim.routes.DutyRepaymentController.onPageLoadCustomsDuty, hasDutyType(Customs)),
    P(ImportVatRepaymentPage, makeclaim.routes.DutyRepaymentController.onPageLoadImportVat, hasDutyType(Vat)),
    P(OtherDutyRepaymentPage, makeclaim.routes.DutyRepaymentController.onPageLoadOtherDuty, hasDutyType(Other)),
    P(UploadPage, makeclaim.routes.UploadFormController.onPageLoad, hasNoUploads),
    P(UploadSummaryPage, makeclaim.routes.UploadFormSummaryController.onPageLoad, hasUploads),
    P(ContactDetailsPage, makeclaim.routes.ContactDetailsController.onPageLoad, always),
    P(AddressPage, makeclaim.routes.AddressController.onPageLoad, always),
    P(ImporterHasEoriNumberPage, makeclaim.routes.ImporterHasEoriController.onPageLoad, isRepresentative),
    P(ImporterEoriNumberPage, makeclaim.routes.ImporterEoriNumberController.onPageLoad, enterImporterEori),
    P(ImporterContactDetailsPage, makeclaim.routes.ImporterDetailsController.onPageLoad, isRepresentative),
    P(RepayToPage, makeclaim.routes.RepayToController.onPageLoad, isRepresentative),
    P(BankDetailsPage, makeclaim.routes.BankDetailsController.onPageLoad, always),
    P(CheckYourAnswersPage, makeclaim.routes.CheckYourAnswersController.onPageLoad, always),
    P(ConfirmationPage, makeclaim.routes.ConfirmationController.onPageLoad, always)
  )

}

protected trait CreateAnswerConditions {

  protected val always: Answers => Boolean = (_: Answers) => true

  protected val hasDutyType: ReclaimDutyType => CreateAnswers => Boolean = (dutyType: ReclaimDutyType) =>
    _.reclaimDutyTypes.contains(dutyType)

  protected val hasNoUploads: CreateAnswers => Boolean = _.uploads.isEmpty

  protected val hasUploads: CreateAnswers => Boolean = _.uploads.nonEmpty

  protected val isRepresentative: CreateAnswers => Boolean = _.isRepresentative

  protected val enterImporterEori: CreateAnswers => Boolean = (answers: CreateAnswers) =>
    isRepresentative(answers) && answers.doesImporterHaveEori

}
