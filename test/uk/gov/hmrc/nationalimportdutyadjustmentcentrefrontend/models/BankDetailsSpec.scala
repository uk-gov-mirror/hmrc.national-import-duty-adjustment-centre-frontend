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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.BankDetails

class BankDetailsSpec extends UnitSpec with TestData {

  "BankDetails" should {

    "pad a 6 digit account number" in {

      BankDetails("Account", "001122", "123456").accountNumber mustBe "00123456"
    }

    "strip spaces and dashes from sort code" in {

      BankDetails("Account", "12 34 56", "12345678").sortCode mustBe "123456"
      BankDetails("Account", "12-34-56", "12345678").sortCode mustBe "123456"
    }

  }

}
