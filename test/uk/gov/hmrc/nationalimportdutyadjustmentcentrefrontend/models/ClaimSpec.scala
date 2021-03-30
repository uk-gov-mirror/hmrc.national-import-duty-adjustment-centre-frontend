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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{Claim, RepayTo, RepresentationType}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{
  CustomsDutyRepaymentPage,
  ImportVatRepaymentPage,
  OtherDutyRepaymentPage,
  RepayToPage
}

class ClaimSpec extends UnitSpec with TestData {

  "Claim" should {

    "error" when {

      "reclaim Customs Duty specified but no calculations present" in {

        intercept[MissingAnswersException] {
          Claim(claimantEori, completeAnswers.copy(reclaimDutyPayments = reclaimDutyPayments - Customs))
        }.getMessage mustBe s"$CustomsDutyRepaymentPage"

      }

      "reclaim Import VAT specified but no calculations present" in {

        intercept[MissingAnswersException] {
          Claim(claimantEori, completeAnswers.copy(reclaimDutyPayments = reclaimDutyPayments - Vat))
        }.getMessage mustBe s"$ImportVatRepaymentPage"

      }

      "reclaim Other Duty specified but no calculations present" in {

        intercept[MissingAnswersException] {
          Claim(claimantEori, completeAnswers.copy(reclaimDutyPayments = reclaimDutyPayments - Other))
        }.getMessage mustBe s"$OtherDutyRepaymentPage"

      }

      "representation type is 'representative' but 'repay to' is missing" in {
        val invalidAnswer =
          completeAnswers.copy(representationType = Some(RepresentationType.Representative), repayTo = None)
        intercept[MissingAnswersException] {
          Claim(claimantEori, invalidAnswer)
        }.getMessage mustBe s"$RepayToPage"

      }
    }

    "calculate the correct total repayment" in {
      val expected =
        customsDutyRepaymentAnswer.dueAmount + importVatRepaymentAnswer.dueAmount + otherDutyRepaymentAnswer.dueAmount

      Claim(claimantEori, completeAnswers).repaymentTotal mustBe expected
    }

    "ignore 'importer being represented' answers when representation type is 'importer'" in {
      val answersWithRepayTo = completeAnswers.copy(
        representationType = Some(RepresentationType.Importer),
        repayTo = Some(RepayTo.Representative)
      )
      Claim(claimantEori, answersWithRepayTo).importerBeingRepresentedDetails mustBe None
    }
  }

}
