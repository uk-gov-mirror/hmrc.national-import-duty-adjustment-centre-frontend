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
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitViewSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.ClaimTypeFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ClaimType
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ClaimType.Quota
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ClaimTypePage

class ClaimTypePageViewSpec extends UnitViewSpec {

  private val page = instanceOf[ClaimTypePage]
  private val form = new ClaimTypeFormProvider().apply()

  private def view(form: Form[ClaimType] = form): Html = page(form)

  "ClaimTypePage on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("claim_type.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("claim_type.title")
    }

    "have radio option for each claim type" in {
      ClaimType.options(form, "claim_type").foreach { item =>
        val inputId = view().getElementsByAttributeValue("value", item.value.get).get(0).id()
        Text(view().getElementsByAttributeValue("for", inputId).text()) mustBe item.content
        view().getElementById(inputId).attr("checked") mustBe empty
      }

    }

    "have 'Continue' button" in {
      view().getElementById("submit") must includeMessage("site.continue")
    }

  }

  "ClaimTypePage on filled form" should {

    "have selected radio option" in {
      val checked = view(form.fill(Quota)).getElementsByAttribute("checked")

      checked.attr("value") mustBe Quota.toString
    }

    "display error when no choice is made" in {

      val errorView = view(form.bind(Map("claim-type" -> "")))

      errorView.getElementsByClass("govuk-error-summary__body").text() mustBe messages("claim_type.error.required")

    }

  }
}
