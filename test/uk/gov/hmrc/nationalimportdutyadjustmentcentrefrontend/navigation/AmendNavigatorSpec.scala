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

import play.api.mvc.Call
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.amendclaim.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

class AmendNavigatorSpec extends UnitSpec with Injector with TestData {

  private val amendNavigator = instanceOf[AmendNavigator]

  private def back(page: Page, userAnswers: AmendAnswers): Call =
    amendNavigator.previousPage(page, userAnswers).maybeCall.getOrElse(Call("GET", "No back page"))

  "Amend Navigator from attach more documents page" when {
    val nextPage     = amendNavigator.nextPage(AttachMoreDocumentsPage, _)
    val previousPage = back(AttachMoreDocumentsPage, _)

    "going forward" should {
      "go to the upload page when answer is yes but no documents uploaded" in {
        nextPage(completeAmendAnswers.copy(uploads = Seq.empty)) mustBe routes.UploadFormController.onPageLoad()
      }

      "go to the upload page when answer is yes but has documents uploaded" in {
        nextPage(completeAmendAnswers) mustBe routes.UploadFormController.onPageLoad()
      }

      "go to the further information page when answer is no" in {
        nextPage(
          completeAmendAnswers.copy(hasMoreDocuments = Some(false))
        ) mustBe routes.FurtherInformationController.onPageLoad()
      }
    }
    "going back" when {
      "go to case reference page" in {
        previousPage(completeAmendAnswers) mustBe routes.CaseReferenceController.onPageLoad()

      }
    }
  }

  "Navigating from further information" when {
    val nextPage     = amendNavigator.nextPage(FurtherInformationPage, _)
    val previousPage = back(FurtherInformationPage, _)
    "going forward" should {
      "go to the check your answers page" in {
        nextPage(completeAmendAnswers) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
    "has more documents is false" when {
      "going back" should {
        "go to the has more documents question page" in {
          previousPage(
            completeAmendAnswers.copy(hasMoreDocuments = Some(false))
          ) mustBe routes.AttachMoreDocumentsController.onPageLoad()

        }
      }
    }
    "has more documents is true" when {
      "going back" should {
        "go to the file upload page" in {
          previousPage(
            completeAmendAnswers.copy(hasMoreDocuments = Some(true))
          ) mustBe routes.UploadFormController.onPageLoad()
        }
      }
    }
  }

  "Navigating from file upload page" when {
    val nextPage     = amendNavigator.nextPage(UploadPage, _)
    val previousPage = back(UploadPage, _)
    "going forward" should {
      "go to the further information page" in {
        nextPage(completeAmendAnswers) mustBe
          routes.FurtherInformationController.onPageLoad()
      }
    }
    "going back" should {
      "go to the has more documents page" in {
        previousPage(completeAmendAnswers) mustBe
          routes.AttachMoreDocumentsController.onPageLoad()
      }
    }
  }

  "Goto Page" should {
    "go directly to named page" in {

      amendNavigator.gotoPage(AmendPageNames.claimReference) mustBe routes.CaseReferenceController.onPageLoad
      amendNavigator.gotoPage(AmendPageNames.attachMoreDocuments) mustBe routes.AttachMoreDocumentsController.onPageLoad
      amendNavigator.gotoPage(AmendPageNames.uploadSummary) mustBe routes.UploadFormController.onPageLoad
      amendNavigator.gotoPage(AmendPageNames.furtherInformation) mustBe routes.FurtherInformationController.onPageLoad
    }
  }

  "Next page when changing answers" should {
    "goto change your answers when no further answers required" in {
      val answers = completeAmendAnswers.copy(changePage = Some(AmendPageNames.claimReference))
      amendNavigator.nextPage(CaseReferencePage, answers) mustBe routes.CheckYourAnswersController.onPageLoad()
    }

    "goto next question with missing answer" in {
      val answers = completeAmendAnswers.copy(
        changePage = Some(AmendPageNames.claimReference),
        uploads = Seq.empty,
        uploadAnotherFile = None
      )
      amendNavigator.nextPage(AttachMoreDocumentsPage, answers) mustBe routes.UploadFormController.onPageLoad()
    }
  }

  "Amend Navigating to first missing answer" should {

    "goto check your answers when no further answers required" in {
      val answers = completeAmendAnswers
      amendNavigator.firstMissingAnswer(answers) mustBe routes.CheckYourAnswersController.onPageLoad
    }

    "find first missing answers" in {
      val answers = completeAmendAnswers.copy(furtherInformation = None)
      amendNavigator.firstMissingAnswer(answers) mustBe routes.FurtherInformationController.onPageLoad
    }
  }
}
