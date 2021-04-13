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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.{CaseReference, FurtherInformation}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create._

trait QuestionPage[A] extends Page

case object RepresentationTypePage     extends QuestionPage[RepresentationType]
case object ClaimTypePage              extends QuestionPage[ClaimType]
case object ContactDetailsPage         extends QuestionPage[ContactDetails]
case object BusinessNamePage     extends QuestionPage[BusinessName]
case object AddressPage                extends QuestionPage[Address]
case object ImporterEoriNumberPage     extends QuestionPage[ItemNumbers]
case object ImporterContactDetailsPage extends QuestionPage[ImporterContactDetails]
case object ReclaimDutyTypePage        extends QuestionPage[Set[ReclaimDutyType]]
case object CustomsDutyRepaymentPage   extends QuestionPage[DutyPaid]
case object ImportVatRepaymentPage     extends QuestionPage[DutyPaid]
case object OtherDutyRepaymentPage     extends QuestionPage[DutyPaid]
case object ReturnAmountSummaryPage    extends QuestionPage[DutyPaid]
case object RepayToPage                extends QuestionPage[RepayTo]
case object BankDetailsPage            extends QuestionPage[BankDetails]
case object ClaimReasonPage            extends QuestionPage[ClaimReason]
case object FurtherInformationPage     extends QuestionPage[FurtherInformation]
case object EntryDetailsPage           extends QuestionPage[EntryDetails]
case object ItemNumbersPage            extends QuestionPage[ItemNumbers]

case object CaseReferencePage       extends QuestionPage[CaseReference]
case object AttachMoreDocumentsPage extends QuestionPage[Boolean]
