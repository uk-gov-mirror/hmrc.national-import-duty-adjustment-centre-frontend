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
  CustomsDutyRepaymentPage,
  ImportVatRepaymentPage,
  Page,
  ReclaimDutyTypePage
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.utils.Injector

class NavigatorSpec extends UnitSpec with Injector with TestData {

  val navigator = instanceOf[Navigator]

  def answers(reclaim: ReclaimDutyType*): UserAnswers = completeAnswers.copy(reclaimDutyTypes = Some(Set(reclaim: _*)))

  def nextPage(page: Page, reclaim: ReclaimDutyType*) =
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
        nextPage(CustomsDutyRepaymentPage, Customs) mustBe routes.BankDetailsController.onPageLoad()
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
        nextPage(ImportVatRepaymentPage, Vat) mustBe routes.BankDetailsController.onPageLoad()
      }
      "user has requested Other Duty repayment" in {
        nextPage(ImportVatRepaymentPage, Vat, Other) mustBe routes.DutyRepaymentController.onPageLoadOtherDuty()
      }
    }
  }
}
