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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, ReclaimDutyType}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._

@Singleton
class CreateNavigator @Inject() () extends Navigator[CreateAnswers] with CreateAnswerConditions {

  override protected val pageOrder: Seq[P] = Seq(
    P(RepresentationTypePage, makeclaim.routes.RepresentationTypeController.onPageLoad, always, defaultHasAnswer),
    P(ClaimTypePage, makeclaim.routes.ClaimTypeController.onPageLoad, always, defaultHasAnswer),
    P(EntryDetailsPage, makeclaim.routes.EntryDetailsController.onPageLoad, always, defaultHasAnswer),
    P(ItemNumbersPage, makeclaim.routes.ItemNumbersController.onPageLoad, always, defaultHasAnswer),
    P(ClaimReasonPage, makeclaim.routes.ClaimReasonController.onPageLoad, always, defaultHasAnswer),
    P(ReclaimDutyTypePage, makeclaim.routes.ReclaimDutyTypeController.onPageLoad, always, defaultHasAnswer),
    P(
      CustomsDutyRepaymentPage,
      makeclaim.routes.DutyRepaymentController.onPageLoadCustomsDuty,
      hasDutyType(Customs),
      defaultHasAnswer
    ),
    P(
      ImportVatRepaymentPage,
      makeclaim.routes.DutyRepaymentController.onPageLoadImportVat,
      hasDutyType(Vat),
      defaultHasAnswer
    ),
    P(
      OtherDutyRepaymentPage,
      makeclaim.routes.DutyRepaymentController.onPageLoadOtherDuty,
      hasDutyType(Other),
      defaultHasAnswer
    ),
    P(UploadPage, makeclaim.routes.UploadFormController.onPageLoad, hasNoUploads, defaultHasAnswer),
    P(UploadSummaryPage, makeclaim.routes.UploadFormSummaryController.onPageLoad, hasUploads, defaultHasAnswer),
    P(ContactDetailsPage, makeclaim.routes.ContactDetailsController.onPageLoad, always, defaultHasAnswer),
    P(AddressPage, makeclaim.routes.AddressController.onPageLoad, always, defaultHasAnswer),
    P(
      ImporterHasEoriNumberPage,
      makeclaim.routes.ImporterHasEoriController.onPageLoad,
      isRepresentative,
      defaultHasAnswer
    ),
    P(
      ImporterEoriNumberPage,
      makeclaim.routes.ImporterEoriNumberController.onPageLoad,
      enterImporterEori,
      defaultHasAnswer
    ),
    P(
      ImporterContactDetailsPage,
      makeclaim.routes.ImporterDetailsController.onPageLoad,
      isRepresentative,
      defaultHasAnswer
    ),
    P(RepayToPage, makeclaim.routes.RepayToController.onPageLoad, isRepresentative, defaultHasAnswer),
    P(BankDetailsPage, makeclaim.routes.BankDetailsController.onPageLoad, always, defaultHasAnswer),
    P(CheckYourAnswersPage, makeclaim.routes.CheckYourAnswersController.onPageLoad, always, defaultHasAnswer),
    P(ConfirmationPage, makeclaim.routes.ConfirmationController.onPageLoad, always, defaultHasAnswer)
  )

  override protected def checkYourAnswersPage: Call = makeclaim.routes.CheckYourAnswersController.onPageLoad

  override protected def pageFor: String => Option[Page] = (pageName: String) =>
    pageName match {
      case _ => None
    }

}

protected trait CreateAnswerConditions {

  protected val defaultHasAnswer: Answers => Boolean = (_: Answers) => false

  protected val always: Answers => Boolean = (_: Answers) => true

  protected val hasDutyType: ReclaimDutyType => CreateAnswers => Boolean = (dutyType: ReclaimDutyType) =>
    _.reclaimDutyTypes.contains(dutyType)

  protected val hasNoUploads: CreateAnswers => Boolean = _.uploads.isEmpty

  protected val hasUploads: CreateAnswers => Boolean = _.uploads.nonEmpty

  protected val isRepresentative: CreateAnswers => Boolean = _.isRepresentative

  protected val enterImporterEori: CreateAnswers => Boolean = (answers: CreateAnswers) =>
    isRepresentative(answers) && answers.doesImporterHaveEori

}
