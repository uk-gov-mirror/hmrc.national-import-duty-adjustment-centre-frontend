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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.eis

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Claim
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingUserAnswersException

class ClaimSpec extends UnitSpec with TestData {

  "Claim" should {

    "error" when {

      "reclaim Customs Duty specified but no calculations present" in {

        intercept[MissingUserAnswersException] {
          Claim(completeAnswers.copy(reclaimDutyPayments = reclaimDutyPayments - Customs))
        }.getMessage mustBe s"Missing answer - DutyPayment $Customs"

      }

      "reclaim Import VAT specified but no calculations present" in {

        intercept[MissingUserAnswersException] {
          Claim(completeAnswers.copy(reclaimDutyPayments = reclaimDutyPayments - Vat))
        }.getMessage mustBe s"Missing answer - DutyPayment $Vat"

      }

      "reclaim Other Duty specified but no calculations present" in {

        intercept[MissingUserAnswersException] {
          Claim(completeAnswers.copy(reclaimDutyPayments = reclaimDutyPayments - Other))
        }.getMessage mustBe s"Missing answer - DutyPayment $Other"

      }
    }

    "calculate the correct total repayment" in {
      val expected =
        customsDutyRepaymentAnswer.dueAmount + importVatRepaymentAnswer.dueAmount + otherDutyRepaymentAnswer.dueAmount

      Claim(completeAnswers).repaymentTotal mustBe expected
    }
  }

}
