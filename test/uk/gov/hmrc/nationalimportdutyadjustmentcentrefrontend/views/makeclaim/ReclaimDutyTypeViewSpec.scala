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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.ReclaimDutyTypeFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ReclaimDutyType.Customs
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ReclaimDutyType
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ReclaimDutyTypeView

class ReclaimDutyTypeViewSpec extends UnitViewSpec {

  private val page = instanceOf[ReclaimDutyTypeView]
  private val form = new ReclaimDutyTypeFormProvider().apply()

  private def view(form: Form[Set[ReclaimDutyType]] = form): Document = page(form, navigatorBack)

  "ReclaimDutyTypePage on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("reclaimDutyType.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("reclaimDutyType.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have checkbox for each claim type" in {
      ReclaimDutyType.options(form, "reclaimDutyType[]").foreach { item =>
        val inputId = view().getElementsByAttributeValue("value", item.value).get(0).id()
        Text(view().getElementsByAttributeValue("for", inputId).text()) mustBe item.content
        view().getElementById(inputId).attr("checked") mustBe empty
      }

    }

    "have 'Continue' button" in {
      view().getElementById("submit") must includeMessage("site.continue")
    }

  }

  "ReclaimDutyTypePage on filled form" should {

    "have selected checkbox option" in {
      val checked = view(form.fill(Set(Customs))).getElementsByAttribute("checked")

      checked.attr("value") mustBe Customs.toString
    }

    "display error when no choice is made" in {

      val errorView = view(form.bind(Map("reclaimDutyType" -> "")))

      errorView.getElementsByClass("govuk-error-summary__body").text() mustBe messages("reclaimDutyType.error.required")

    }

  }
}
