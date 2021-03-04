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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.bars

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{BarsTestData, UnitSpec}

class MetadataResponseSpec extends UnitSpec with BarsTestData {

  "MetadataResponse" should {

    "accept BACS if metadata is valid" in {
      validMetaDataResponse.acceptsBacsPayments mustBe true
    }

    "accept BACS if office status M" in {
      validMetaDataResponse.copy(bacsOfficeStatus = "M").acceptsBacsPayments mustBe true
    }

    "accept BACS if office status A" in {
      validMetaDataResponse.copy(bacsOfficeStatus = "A").acceptsBacsPayments mustBe true
    }

    "block BACS if office status N" in {
      validMetaDataResponse.copy(bacsOfficeStatus = "N").acceptsBacsPayments mustBe false
    }

    "accept BACS if disallowed transactions contains DR" in {
      validMetaDataResponse.copy(disallowedTransactions = Seq("DR")).acceptsBacsPayments mustBe true
    }

    "block BACS if disallowed transactions contains CR" in {
      validMetaDataResponse.copy(disallowedTransactions = Seq("DR", "CR")).acceptsBacsPayments mustBe false
    }
  }
}
