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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{ReclaimDutyType, UserAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

class NavigatorSpec extends UnitSpec with Injector with TestData {

  private val navigator = instanceOf[Navigator]

  private def answers(reclaim: ReclaimDutyType*): UserAnswers =
    completeAnswers.copy(reclaimDutyTypes = Some(Set(reclaim: _*)))

  private def back(page: Page, userAnswers: UserAnswers): Call =
    navigator.previousPage(page, userAnswers).getOrElse(Call("GET", "No back page"))

  "Navigator from address page" when {
    val nextPage     = navigator.nextPage(AddressPage, _)
    val previousPage = back(AddressPage, _)

    "going forward" should {
      "go to bank details page" in {
        nextPage(answers()) mustBe routes.BankDetailsController.onPageLoad()
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
      "go to item number page" in {
        previousPage(answers()) mustBe routes.ItemNumbersController.onPageLoad()
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
        nextPage(answers(Customs)) mustBe routes.UploadFormController.onPageLoad()
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
        nextPage(answers(Customs, Vat)) mustBe routes.UploadFormController.onPageLoad()
        nextPage(answers(Vat)) mustBe routes.UploadFormController.onPageLoad()
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
        nextPage(answers(Customs, Other)) mustBe routes.UploadFormController.onPageLoad()
        nextPage(answers(Vat, Other)) mustBe routes.UploadFormController.onPageLoad()
        nextPage(answers(Customs, Vat, Other)) mustBe routes.UploadFormController.onPageLoad()
        nextPage(answers(Other)) mustBe routes.UploadFormController.onPageLoad()
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
        previousPage(answers()) mustBe routes.AddressController.onPageLoad()
      }
    }
  }
}
