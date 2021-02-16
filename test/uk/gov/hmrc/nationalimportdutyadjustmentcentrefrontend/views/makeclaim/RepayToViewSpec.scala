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

import org.jsoup.nodes.Document
import play.api.data.Form
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitViewSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.RepayToFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.RepayTo
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.RepayTo.Importer
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.RepayToView

class RepayToViewSpec extends UnitViewSpec {

  private val page = instanceOf[RepayToView]
  private val form = new RepayToFormProvider().apply()

  private def view(form: Form[RepayTo] = form): Document = page(form, navigatorBack)

  "RepayToView on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("repay_to.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("repay_to.title")
    }

    "have radio option for each representation type" in {
      RepayTo.options(form, "repay_to").foreach { item =>
        val inputId = view().getElementsByAttributeValue("value", item.value.get).get(0).id()
        Text(view().getElementsByAttributeValue("for", inputId).text()) mustBe item.content
        view().getElementById(inputId).attr("checked") mustBe empty
      }

    }

    "have 'Continue' button" in {
      view().getElementById("submit") must includeMessage("site.continue")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

  }

  "RepayToView on filled form" should {

    "have selected radio option" in {
      val checked = view(form.fill(Importer)).getElementsByAttribute("checked")

      checked.attr("value") mustBe Importer.toString
    }

    "display error when no choice is made" in {

      val errorView = view(form.bind(Map("repay_to" -> "")))

      errorView.getElementsByClass("govuk-error-summary__body").text() mustBe messages("repay_to.error.required")

    }

  }
}
