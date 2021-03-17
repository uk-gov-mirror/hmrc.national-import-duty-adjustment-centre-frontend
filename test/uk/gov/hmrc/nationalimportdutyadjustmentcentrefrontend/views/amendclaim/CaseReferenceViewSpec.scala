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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.amendclaim

import org.jsoup.nodes.Document
import play.api.data.Form
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitViewSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.amend.CaseReferenceFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.CaseReference
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendclaim.CaseReferenceView

class CaseReferenceViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[CaseReferenceView]
  private val form = new CaseReferenceFormProvider().apply()

  private def view(form: Form[CaseReference] = form): Document = page(form, navigatorBack)

  "CaseReferenceView on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("amend.case.reference.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("amend.case.reference.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have label for case reference" in {
      view().getElementsByAttributeValue("for", "caseReference") must containMessage("amend.case.reference.title")
    }

    "have 'Continue' button" in {
      view().getElementById("nidac-continue") must includeMessage("site.continue")
    }

  }

  "CaseReferenceView on filled form" should {

    "have populated fields" in {
      val filledView = view(form.fill(caseReferenceAnswer))

      filledView.getElementById("caseReference") must haveValue(caseReferenceAnswer.number)
    }

    "display error when " when {

      "case reference missing" in {
        val errorView = view(form.bind(Map("caseReference" -> "")))
        errorView must haveFieldError("caseReference", "amend.case.reference.error.required")
        errorView must havePageError("amend.case.reference.error.required")
      }

    }

  }
}
