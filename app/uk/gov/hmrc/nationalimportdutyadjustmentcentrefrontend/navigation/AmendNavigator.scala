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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.amendclaim.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Answers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._

@Singleton
class AmendNavigator @Inject() () extends Navigator[AmendAnswers] with AmendAnswerConditions {

  override protected val pageOrder: Seq[P] = Seq(
    P(CaseReferencePage, routes.CaseReferenceController.onPageLoad, always),
    P(AttachMoreDocumentsPage, routes.AttachMoreDocumentsController.onPageLoad, always),
    P(UploadPage, routes.UploadFormController.onPageLoad, showUploadDocuments),
    P(FurtherInformationPage, routes.FurtherInformationController.onPageLoad, always),
    P(CheckYourAnswersPage, routes.CheckYourAnswersController.onPageLoad, always)
  )

}

protected trait AmendAnswerConditions {

  protected val always: Answers => Boolean = (_: Answers) => true

  protected val showUploadDocuments: AmendAnswers => Boolean = _.hasMoreDocuments.contains(true)

}
