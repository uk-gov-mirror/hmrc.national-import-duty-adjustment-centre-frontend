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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.makeclaim

import play.twirl.api.Html
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitViewSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ConfirmationPage

import scala.util.Random

class ConfirmationPageViewSpec extends UnitViewSpec {

  private val page = instanceOf[ConfirmationPage]

  private val claimReference = Random.alphanumeric.take(16).mkString
  private val view: Html     = page(claimReference)

  "ConfirmationPage" should {

    "have correct title" in {
      view.title() must startWith(messages("confirmation.title"))
    }

    "have correct heading" in {
      view.getElementsByTag("h1") must containMessage("confirmation.title")
    }

    "have correct reference" in {
      view.getElementsByClass("govuk-panel__body").text() must include(claimReference)
    }

  }
}
