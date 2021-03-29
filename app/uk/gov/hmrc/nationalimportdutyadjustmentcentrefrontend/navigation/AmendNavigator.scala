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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.amendclaim.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Answers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._

@Singleton
class AmendNavigator @Inject() ()
    extends Navigator[AmendAnswers] with AmendAnswerConditions with AmendHasAnsweredConditions {

  override protected val pageOrder: Seq[P] = Seq(
    P(FirstPage, routes.CaseReferenceController.onPageLoad, never, always),
    P(CaseReferencePage, routes.CaseReferenceController.onPageLoad, always, caseReferenceAnswered),
    P(AttachMoreDocumentsPage, routes.AttachMoreDocumentsController.onPageLoad, always, attachMoreDocumentsAnswered),
    P(UploadPage, routes.UploadFormController.onPageLoad, showUploadDocuments, uploadPageAnswered),
    P(UploadSummaryPage, routes.UploadFormSummaryController.onPageLoad, showUploadSummary, uploadSummaryPageAnswered),
    P(FurtherInformationPage, routes.FurtherInformationController.onPageLoad, always, furtherInformationAnswered),
    P(CheckYourAnswersPage, routes.CheckYourAnswersController.onPageLoad, always, never),
    P(ConfirmationPage, routes.ConfirmationController.onPageLoad, always, never)
  )

  override protected def checkYourAnswersPage: Call = routes.CheckYourAnswersController.onPageLoad

  override protected def pageFor: String => Option[Page] = (pageName: String) =>
    pageName match {
      case AmendPageNames.claimReference      => Some(CaseReferencePage)
      case AmendPageNames.attachMoreDocuments => Some(AttachMoreDocumentsPage)
      case AmendPageNames.uploadSummary       => Some(UploadSummaryPage)
      case AmendPageNames.furtherInformation  => Some(FurtherInformationPage)
      case _                                  => None
    }

}

protected trait AmendAnswerConditions {

  protected val always: Answers => Boolean = (_: Answers) => true

  protected val showUploadDocuments: AmendAnswers => Boolean = (answers: AmendAnswers) =>
    answers.hasMoreDocuments.contains(true) && answers.uploads.isEmpty

  protected val showUploadSummary: AmendAnswers => Boolean = (answers: AmendAnswers) =>
    answers.hasMoreDocuments.contains(true) && answers.uploads.nonEmpty

}

protected trait AmendHasAnsweredConditions {

  protected val never: Answers => Boolean = (_: Answers) => false

  protected val caseReferenceAnswered: AmendAnswers => Boolean       = _.caseReference.nonEmpty
  protected val attachMoreDocumentsAnswered: AmendAnswers => Boolean = _.hasMoreDocuments.nonEmpty
  protected val furtherInformationAnswered: AmendAnswers => Boolean  = _.furtherInformation.nonEmpty

  protected val uploadPageAnswered: AmendAnswers => Boolean = (answers: AmendAnswers) =>
    answers.hasMoreDocuments.contains(true) && answers.uploads.nonEmpty

  protected val uploadSummaryPageAnswered: AmendAnswers => Boolean = (answers: AmendAnswers) =>
    answers.hasMoreDocuments.contains(true) && answers.uploads.nonEmpty && answers.uploadAnotherFile.contains(false)

}

object AmendPageNames {
  val claimReference      = "claim-reference"
  val attachMoreDocuments = "attach-more-documents"
  val uploadSummary       = "your-uploads"
  val furtherInformation  = "additional-information"
}
