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

import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitViewSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.YesNoFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ImporterHasEoriView

class ImporterHasEoriViewSpec extends UnitViewSpec {

  private val page = instanceOf[ImporterHasEoriView]
  private val form = new YesNoFormProvider().apply("importer.has.eori.required")

  private def view(form: Form[Boolean] = form): Html = page(form, navigatorBack)

  "ImporterhasEoriPage on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("importer.has.eori.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("importer.has.eori.title")
    }

    "have radio options for yes and no" in {
      val yesRadio = view().getElementsByAttributeValue("name", "yesOrNo").get(0)
      yesRadio.attr("value") mustBe "true"

      val noRadio = view().getElementsByAttributeValue("name", "yesOrNo").get(1)
      noRadio.attr("value") mustBe "false"

    }

    "have 'Continue' button" in {
      view().getElementById("submit") must includeMessage("site.continue")
    }

  }

  "ImporterhasEoriPage on filled form" should {

    "have selected yes radio option if true" in {
      val checked = view(form.fill(true)).getElementsByAttribute("checked")

      checked.attr("value") mustBe "true"
    }

    "have selected no radio option if true" in {
      val checked = view(form.fill(false)).getElementsByAttribute("checked")

      checked.attr("value") mustBe "false"
    }

    "display error when no choice is made" in {

      val errorView = view(form.bind(Map("yesOrNo" -> "")))

      errorView.getElementsByClass("govuk-error-summary__body").text() mustBe messages(
        "importer.has.eori.required"
      )

    }

  }
}
