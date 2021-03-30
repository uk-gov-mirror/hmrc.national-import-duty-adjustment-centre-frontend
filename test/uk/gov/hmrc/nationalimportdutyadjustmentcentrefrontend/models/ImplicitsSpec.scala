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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Implicits.SanitizedString

class ImplicitsSpec extends UnitSpec with TestData {

  "Implicits.SanitizedString" should {

    "left-pad a sort code" in {
      "123456".leftPadAccountNumber() mustBe "00123456"
      "0123456".leftPadAccountNumber() mustBe "00123456"
    }

    "strip spaces" in {
      "12 34 56".stripSpacesAndDashes() mustBe "123456"
    }

    "strip dashes" in {
      "12-34-56".stripSpacesAndDashes() mustBe "123456"
    }

    "strip spaces and dashes" in {
      "12 - 34 - 56".stripSpacesAndDashes() mustBe "123456"
    }

    "removes leading space" in {
      "         hello".stripExternalAndReduceInternalSpaces() mustBe "hello"
    }

    "removes trailing space" in {
      "hello            ".stripExternalAndReduceInternalSpaces() mustBe "hello"
    }

    "removes leading and trailing space" in {
      "         hello          ".stripExternalAndReduceInternalSpaces() mustBe "hello"
    }

    "reduces internal space" in {
      "hi           there".stripExternalAndReduceInternalSpaces() mustBe "hi there"
    }

    "removes leading space and reduces internal space" in {
      "    erm     hello".stripExternalAndReduceInternalSpaces() mustBe "erm hello"
    }

    "removes trailing space and reduces internal space" in {
      "erm         hello               ".stripExternalAndReduceInternalSpaces() mustBe "erm hello"
    }

    "removes trailing and leading space and reduces internal space" in {
      "              erm         hello               ".stripExternalAndReduceInternalSpaces() mustBe "erm hello"
    }

    "reduces multiple internal spaces" in {
      "         erm         hello    there           ".stripExternalAndReduceInternalSpaces() mustBe "erm hello there"
    }

    "makes no unexpected changes" in {
      "Hi There".stripExternalAndReduceInternalSpaces() mustBe "Hi There"
    }
  }

}
