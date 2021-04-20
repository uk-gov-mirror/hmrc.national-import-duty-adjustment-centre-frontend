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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views

import org.jsoup.nodes.Document
import play.api.data.Form
import uk.gov.hmrc.govukfrontend.views.Aliases.Text
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitViewSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.WhatDoYouWantToDoFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ToDoType
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ToDoType.NewClaim
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.WhatDoYouWantToDoPage

class WhatDoYouWantToDoPageViewSpec extends UnitViewSpec {

  private val page = instanceOf[WhatDoYouWantToDoPage]
  private val form = new WhatDoYouWantToDoFormProvider().apply()

  private def view(form: Form[ToDoType] = form): Document = page(form)

  "WhatDoYouWantToDoPage on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("what_do_you_want_to_do.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("what_do_you_want_to_do.title")
    }

    "have radio option for each todo type" in {
      ToDoType.options(form, "what_do_you_want_to_do").foreach { item =>
        val inputId = view().getElementsByAttributeValue("value", item.value.get).get(0).id()
        Text(view().getElementsByAttributeValue("for", inputId).text()) mustBe item.content
        view().getElementById(inputId).attr("checked") mustBe empty
      }

    }

    "have 'Continue' button" in {
      view().getElementById("nidac-continue") must includeMessage("site.continue")
    }

    "have beta phase banner" in {
      val banner = view().getElementsByClass("govuk-phase-banner")
      banner.text() must include("beta")
      val feedbackLink = banner.first().getElementsByClass("govuk-link")

      feedbackLink must containMessage("phase.banner.link")
      feedbackLink.attr("href") must include("contact/beta-feedback")
      feedbackLink.attr("href") must include("service=national-import-duty-adjustment-centre")
    }

    "have `page not working` link" in {
      val technicalIssue = view().getElementsByClass("nidac-report-technical-issue")
      technicalIssue.text() must include("page not working")
      val link = technicalIssue.first().getElementsByClass("govuk-link")

      link.attr("href") must include("contact/problem_reports_nonjs")
      link.attr("href") must include("service=national-import-duty-adjustment-centre")
    }

  }

  "WhatDoYouWantToDoPage on filled form" should {

    "have selected radio options" in {
      val checked = view(form.fill(NewClaim)).getElementsByAttribute("checked")
      checked.attr("value") mustBe NewClaim.toString
    }

    "display error when no choice is made" in {
      val errorView = view(form.bind(Map("what_do_you_want_to_do" -> "")))
      errorView must havePageError("what_do_you_want_to_do.error.required")
    }

  }
}
