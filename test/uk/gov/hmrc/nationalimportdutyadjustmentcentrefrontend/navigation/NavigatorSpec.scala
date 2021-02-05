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

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.makeclaim.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{ReclaimDutyType, UserAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{
  AddressPage,
  BankDetailsPage,
  CustomsDutyRepaymentPage,
  ImportVatRepaymentPage,
  OtherDutyRepaymentPage,
  Page,
  ReclaimDutyTypePage
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

class NavigatorSpec extends UnitSpec with Injector with TestData {

  private val navigator = instanceOf[Navigator]

  private def answers(reclaim: ReclaimDutyType*): UserAnswers = completeAnswers.copy(reclaimDutyTypes = Some(Set(reclaim: _*)))

  private def nextPage(page: Page, reclaim: ReclaimDutyType*) =
    navigator.nextPage(page, answers(reclaim: _*))

  "Navigator on leaving ReclaimDutyTypePage" should {

    "redirect to the correct repayment calculation page" when {

      "user has requested Customs Duty repayment" in {
        nextPage(ReclaimDutyTypePage, Customs, Vat, Other) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
      }
      "user has requested Import VAT repayment" in {
        nextPage(ReclaimDutyTypePage, Vat, Other) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
      }
      "user has requested Other Duty repayment" in {
        nextPage(ReclaimDutyTypePage, Other) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
    }
  }

  "Navigator on leaving CustomsDutyRepaymentPage" should {

    "redirect to the correct repayment calculation page" when {

      "user has ONLY requested Customs Duty repayment" in {
        nextPage(CustomsDutyRepaymentPage, Customs) mustBe routes.UploadFormController.onPageLoad()
      }
      "user has requested Import VAT repayment" in {
        nextPage(
          CustomsDutyRepaymentPage,
          Customs,
          Vat,
          Other
        ) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
      }
      "user has requested Other Duty repayment" in {
        nextPage(CustomsDutyRepaymentPage, Customs, Other) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
    }
  }

  "Navigator on leaving ImportVatRepaymentPage" should {

    "redirect to the correct repayment calculation page" when {

      "user has ONLY requested Import VAT repayment" in {
        nextPage(ImportVatRepaymentPage, Vat) mustBe routes.UploadFormController.onPageLoad()
      }
      "user has requested Other Duty repayment" in {
        nextPage(ImportVatRepaymentPage, Vat, Other) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
    }
  }

  "Navigator from address page" when {
    "going forward" should {
      "go to bank details page" in {
        navigator.nextPage(AddressPage, answers()) mustBe routes.BankDetailsController.onPageLoad()
      }
    }
    "going back" should {
      "go to contact details page" in {
        navigator.previousPage(AddressPage, answers()) mustBe routes.ContactDetailsController.onPageLoad()
      }
    }
  }

  "Navigator from duty types page" when {
    "going forward" should {
      "go to customs duty page when Customs duty type selected" in {
        navigator.nextPage(
          ReclaimDutyTypePage,
          answers(Customs)
        ) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
        navigator.nextPage(
          ReclaimDutyTypePage,
          answers(Customs, Vat)
        ) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
        navigator.nextPage(
          ReclaimDutyTypePage,
          answers(Customs, Other)
        ) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
        navigator.nextPage(
          ReclaimDutyTypePage,
          answers(Customs, Vat, Other)
        ) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
      }
      "go to vat duty page when VAT duty type selected (and Customs duty type not selected)" in {
        navigator.nextPage(
          ReclaimDutyTypePage,
          answers(Vat)
        ) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
        navigator.nextPage(
          ReclaimDutyTypePage,
          answers(Vat, Other)
        ) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
      }
      "go to vat duty page when only OTHER duty type selected" in {
        navigator.nextPage(
          ReclaimDutyTypePage,
          answers(Other)
        ) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
    }
    "going back" should {
      "go to item number page" in {
        navigator.previousPage(ReclaimDutyTypePage, answers()) mustBe routes.ItemNumbersController.onPageLoad()
      }
    }
  }

  "Navigator from Customs duty page" when {
    "going forward" should {
      "go to VAT duty page when VAT duty type selected" in {
        navigator.nextPage(
          CustomsDutyRepaymentPage,
          answers(Customs, Vat)
        ) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
        navigator.nextPage(
          CustomsDutyRepaymentPage,
          answers(Customs, Vat, Other)
        ) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
      }
      "go to Other duty page when Other duty selected and VAT duty not selected" in {
        navigator.nextPage(
          CustomsDutyRepaymentPage,
          answers(Customs, Other)
        ) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
      "go to upload page when neither VAT or Other duty selected" in {
        navigator.nextPage(CustomsDutyRepaymentPage, answers(Customs)) mustBe routes.UploadFormController.onPageLoad()
      }
    }
    "going back" should {
      "go to duty types page" in {
        navigator.previousPage(
          CustomsDutyRepaymentPage,
          answers(Customs)
        ) mustBe routes.ReclaimDutyTypeController.onPageLoad()
        navigator.previousPage(
          CustomsDutyRepaymentPage,
          answers(Customs, Vat)
        ) mustBe routes.ReclaimDutyTypeController.onPageLoad()
        navigator.previousPage(
          CustomsDutyRepaymentPage,
          answers(Customs, Vat, Other)
        ) mustBe routes.ReclaimDutyTypeController.onPageLoad()
        navigator.previousPage(
          CustomsDutyRepaymentPage,
          answers(Customs, Other)
        ) mustBe routes.ReclaimDutyTypeController.onPageLoad()
      }
    }
  }

  "Navigator from VAT duty page" when {
    "going forward" should {
      "go to other duty page when Other duty type selected" in {
        navigator.nextPage(
          ImportVatRepaymentPage,
          answers(Customs, Vat, Other)
        ) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
        navigator.nextPage(
          ImportVatRepaymentPage,
          answers(Vat, Other)
        ) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
      "go to upload page when Other duty not selected" in {
        navigator.nextPage(
          ImportVatRepaymentPage,
          answers(Customs, Vat)
        ) mustBe routes.UploadFormController.onPageLoad()
        navigator.nextPage(ImportVatRepaymentPage, answers(Vat)) mustBe routes.UploadFormController.onPageLoad()
      }
    }
    "going back" should {
      "go to customs duty page when Customs duty type selected" in {
        navigator.previousPage(
          ImportVatRepaymentPage,
          answers(Customs, Vat)
        ) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
        navigator.previousPage(
          ImportVatRepaymentPage,
          answers(Customs, Vat, Other)
        ) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
      }

      "go to duty types page when Customs duty type not selected" in {
        navigator.previousPage(
          ImportVatRepaymentPage,
          answers(Vat)
        ) mustBe routes.ReclaimDutyTypeController.onPageLoad()
        navigator.previousPage(
          ImportVatRepaymentPage,
          answers(Vat, Other)
        ) mustBe routes.ReclaimDutyTypeController.onPageLoad()
      }
    }
  }

  "Navigator from Other duty page" when {
    "going forward" should {
      "go to upload page" in {
        navigator.nextPage(
          OtherDutyRepaymentPage,
          answers(Customs, Other)
        ) mustBe routes.UploadFormController.onPageLoad()
        navigator.nextPage(OtherDutyRepaymentPage, answers(Vat, Other)) mustBe routes.UploadFormController.onPageLoad()
        navigator.nextPage(
          OtherDutyRepaymentPage,
          answers(Customs, Vat, Other)
        ) mustBe routes.UploadFormController.onPageLoad()
        navigator.nextPage(OtherDutyRepaymentPage, answers(Other)) mustBe routes.UploadFormController.onPageLoad()
      }
    }
    "going back" should {
      "go to Vat duty page when Vat duty type selected" in {
        navigator.previousPage(
          OtherDutyRepaymentPage,
          answers(Customs, Vat, Other)
        ) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
        navigator.previousPage(
          OtherDutyRepaymentPage,
          answers(Vat, Other)
        ) mustBe routes.DutyRepaymentController.onPageLoadImportVat()
      }

      "go to Customs duty page when Customs duty type selected and Vat duty type not selected" in {
        navigator.previousPage(
          OtherDutyRepaymentPage,
          answers(Customs, Other)
        ) mustBe routes.DutyRepaymentController.onPageLoadCustomsDuty()
      }

      "go to duty types page when neither Customs or Vat duty type not selected" in {
        navigator.previousPage(
          OtherDutyRepaymentPage,
          answers(Other)
        ) mustBe routes.ReclaimDutyTypeController.onPageLoad()
      }
    }
  }

  "Navigator from bank details page" when {
    "going forward" should {
      "go to check your answers page" in {
        navigator.nextPage(BankDetailsPage, answers()) mustBe routes.CheckYourAnswersController.onPageLoad()
      }
    }
    "going back" should {
      "go to address page" in {
        navigator.previousPage(BankDetailsPage, answers()) mustBe routes.AddressController.onPageLoad()
      }
    }
  }
}
