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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitViewSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.EoriNumberFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.EoriNumber
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ImporterEoriNumberView

class ImporterEoriNumberViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[ImporterEoriNumberView]
  private val form = new EoriNumberFormProvider().apply()

  private def view(form: Form[EoriNumber] = form): Document = page(form, navigatorBack)

  "ImporterEoriNumberView on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("importer.eori.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("importer.eori.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have label for EORI number" in {
      view().getElementsByAttributeValue("for", "eoriNumber") must containMessage("importer.eori.title")
    }

    "have 'Continue' button" in {
      view().getElementById("submit") must includeMessage("site.continue")
    }

  }

  "ImporterEoriNumberView on filled form" should {

    "have populated fields" in {
      val filledView = view(form.fill(importerEoriNumberAnswer))

      filledView.getElementById("eoriNumber") must haveValue(importerEoriNumberAnswer.number)
    }

    "display error when " when {

      "eori number missing" in {
        view(form.bind(Map("eoriNumber" -> ""))) must haveFieldError("eoriNumber", "importer.eori.error.required")
      }

    }

  }
}
