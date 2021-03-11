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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.makeclaim.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{
  CreateAnswers,
  ReclaimDutyType,
  RepresentationType
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.upscan.UploadedFile
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

class CreateNavigatorSpec extends UnitSpec with Injector with TestData {

  private val navigator = instanceOf[CreateNavigator]

  private def answers(reclaim: ReclaimDutyType*): CreateAnswers =
    completeAnswers.copy(reclaimDutyTypes = Set(reclaim: _*))

  private def back(page: Page, userAnswers: CreateAnswers): Call =
    navigator.previousPage(page, userAnswers).maybeCall.getOrElse(Call("GET", "No back page"))

  "Navigator from address page" when {
    val nextPage     = navigator.nextPage(AddressPage, _)
    val previousPage = back(AddressPage, _)

    "going forward" should {
      "go to bank details page if claimant is importer" in {
        nextPage(
          answers().copy(representationType = Some(RepresentationType.Importer))
        ) mustBe routes.BankDetailsController.onPageLoad()
      }
      "go to does importer have Eori page if claimant is representative" in {
        nextPage(
          answers().copy(representationType = Some(RepresentationType.Representative))
        ) mustBe routes.ImporterHasEoriController.onPageLoad()
      }
    }
    "going back" should {
      "go to contact details page" in {
        previousPage(answers()) mustBe routes.ContactDetailsController.onPageLoad()
      }
    }
  }

  "Navigator from duty types page" when {
    val nextPage     = navigator.nextPage(ReclaimDutyTypePage, _)
    val previousPage = back(ReclaimDutyTypePage, _)

    "going forward" should {
      "go to customs duty page when Customs duty type selected" in {
        nextPage(answers(Customs)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
        nextPage(answers(Customs, Vat)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
        nextPage(answers(Customs, Other)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
        nextPage(answers(Customs, Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
      }
      "go to vat duty page when VAT duty type selected (and Customs duty type not selected)" in {
        nextPage(answers(Vat)) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
        nextPage(answers(Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
      }
      "go to vat duty page when only OTHER duty type selected" in {
        nextPage(answers(Other)) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
    }
    "going back" should {
      "go to claim reason page" in {
        previousPage(answers()) mustBe routes.ClaimReasonController.onPageLoad()
      }
    }
  }

  "Navigator from Customs duty page" when {
    val nextPage     = navigator.nextPage(CustomsDutyRepaymentPage, _)
    val previousPage = back(CustomsDutyRepaymentPage, _)

    "going forward" should {
      "go to vat duty page when VAT duty type selected" in {
        nextPage(answers(Customs, Vat)) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
        nextPage(answers(Customs, Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
      }
      "go to other duty page when Other duty selected and VAT duty not selected" in {
        nextPage(answers(Customs, Other)) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
      "go to upload page when neither VAT or Other duty selected" in {
        nextPage(answers(Customs)) mustBe routes.UploadFormSummaryController.onPageLoad()
      }
    }
    "going back" should {
      "go to duty types page" in {
        previousPage(answers(Customs)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
        previousPage(answers(Customs, Vat)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
        previousPage(answers(Customs, Vat, Other)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
        previousPage(answers(Customs, Other)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
      }
    }
  }

  "Navigator from VAT duty page" when {
    val nextPage     = navigator.nextPage(ImportVatRepaymentPage, _)
    val previousPage = back(ImportVatRepaymentPage, _)

    "going forward" should {
      "go to other duty page when Other duty type selected" in {
        nextPage(answers(Customs, Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
        nextPage(answers(Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
      "go to upload page when Other duty not selected" in {
        nextPage(answers(Customs, Vat)) mustBe routes.UploadFormSummaryController.onPageLoad()
        nextPage(answers(Vat)) mustBe routes.UploadFormSummaryController.onPageLoad()
      }
    }
    "going back" should {
      "go to customs duty page when Customs duty type selected" in {
        previousPage(answers(Customs, Vat)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
        previousPage(answers(Customs, Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
      }

      "go to duty types page when Customs duty type not selected" in {
        previousPage(answers(Vat)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
        previousPage(answers(Vat, Other)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
      }
    }
  }

  "Navigator from Other duty page" when {
    val nextPage     = navigator.nextPage(OtherDutyRepaymentPage, _)
    val previousPage = back(OtherDutyRepaymentPage, _)

    "going forward" should {
      "go to upload page" in {
        nextPage(answers(Customs, Other)) mustBe routes.UploadFormSummaryController.onPageLoad()
        nextPage(answers(Vat, Other)) mustBe routes.UploadFormSummaryController.onPageLoad()
        nextPage(answers(Customs, Vat, Other)) mustBe routes.UploadFormSummaryController.onPageLoad()
        nextPage(answers(Other)) mustBe routes.UploadFormSummaryController.onPageLoad()
      }
    }
    "going back" should {
      "go to vat duty page when Vat duty type selected" in {
        previousPage(answers(Customs, Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
        previousPage(answers(Vat, Other)) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
      }

      "go to customs duty page when Customs duty type selected and Vat duty type not selected" in {
        previousPage(answers(Customs, Other)) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
      }

      "go to duty types page when neither Customs or Vat duty type not selected" in {
        previousPage(answers(Other)) mustBe routes.ReclaimDutyTypeController.onPageLoad()
      }
    }
  }

  "Navigator from bank details page" when {
    val nextPage     = navigator.nextPage(BankDetailsPage, _)
    val previousPage = back(BankDetailsPage, _)

    "going forward" should {
      "go to check your answers page" in {
        nextPage(answers()) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
    "going back" should {
      "go to address page" in {
        previousPage(
          answers().copy(representationType = Some(RepresentationType.Importer))
        ) mustBe routes.AddressController.onPageLoad()
        previousPage(
          answers().copy(representationType = Some(RepresentationType.Representative))
        ) mustBe routes.RepayToController.onPageLoad()
      }
    }
  }

  "Navigating around file uploads" when {

    def answers(uploads: Seq[UploadedFile]): CreateAnswers =
      completeAnswers.copy(uploads = uploads)

    val nextPage     = navigator.nextPage(OtherDutyRepaymentPage, _)
    val previousPage = back(ContactDetailsPage, _)

    "going forward (from the question before file uploads)" when {
      "no files have been uploaded" should {
        "goto upload page" in {
          nextPage(answers(Seq.empty)) mustBe routes.UploadFormController.onPageLoad()
        }
      }
      "files have been uploaded" should {
        "goto upload summary page" in {
          nextPage(answers(Seq(uploadAnswer))) mustBe routes.UploadFormSummaryController.onPageLoad()
        }
      }
    }
    "going back (from the question after file uploads)" when {
      "no files have been uploaded" should {
        "goto upload page" in {
          previousPage(answers(Seq.empty)) mustBe routes.UploadFormController.onPageLoad()
        }
      }
      "files have been uploaded" should {
        "goto upload summary page" in {
          previousPage(answers(Seq(uploadAnswer))) mustBe routes.UploadFormSummaryController.onPageLoad()
        }
      }
    }
  }

  "Navigating to page" should {
    "go directly to named page" in {

      navigator.gotoPage(CreatePageNames.representationType) mustBe routes.RepresentationTypeController.onPageLoad
      navigator.gotoPage(CreatePageNames.claimType) mustBe routes.ClaimTypeController.onPageLoad
      navigator.gotoPage(CreatePageNames.entryDetails) mustBe routes.EntryDetailsController.onPageLoad
      navigator.gotoPage(CreatePageNames.itemNumbers) mustBe routes.ItemNumbersController.onPageLoad
      navigator.gotoPage(CreatePageNames.claimReason) mustBe routes.ClaimReasonController.onPageLoad
      navigator.gotoPage(CreatePageNames.dutyTypes) mustBe routes.ReclaimDutyTypeController.onPageLoad
      navigator.gotoPage(CreatePageNames.uploadSummary) mustBe routes.UploadFormSummaryController.onPageLoad
      navigator.gotoPage(CreatePageNames.contactDetails) mustBe routes.ContactDetailsController.onPageLoad
      navigator.gotoPage(CreatePageNames.contactAddress) mustBe routes.AddressController.onPageLoad
      navigator.gotoPage(CreatePageNames.importerHasEori) mustBe routes.ImporterHasEoriController.onPageLoad
      navigator.gotoPage(CreatePageNames.importerEori) mustBe routes.ImporterEoriNumberController.onPageLoad
      navigator.gotoPage(CreatePageNames.importerDetails) mustBe routes.ImporterDetailsController.onPageLoad
      navigator.gotoPage(CreatePageNames.repayTo) mustBe routes.RepayToController.onPageLoad
      navigator.gotoPage(CreatePageNames.bankDetails) mustBe routes.BankDetailsController.onPageLoad
    }
  }

  "Navigating to next page when changing answers" should {

    "goto change your answers when no further answers required" in {
      val answers = completeAnswers.copy(changePage = Some(CreatePageNames.claimReason))
      navigator.nextPage(ClaimReasonPage, answers) mustBe routes.CheckYourAnswersController.onPageLoad
    }

    "goto importer EORI page when changing from no to yes" in {
      val answers = completeAnswers.copy(
        importerEori = None,
        changePage = Some(CreatePageNames.importerHasEori),
        importerHasEori = Some(true)
      )
      navigator.nextPage(ImporterHasEoriNumberPage, answers) mustBe routes.ImporterEoriNumberController.onPageLoad
    }

    "goto does importer have EORI when changing from Importer to Representative" in {
      val answers = importerAnswers.copy(
        changePage = Some(CreatePageNames.representationType),
        representationType = Some(RepresentationType.Representative)
      )
      navigator.nextPage(RepresentationTypePage, answers) mustBe routes.ImporterHasEoriController.onPageLoad
    }
  }
}
