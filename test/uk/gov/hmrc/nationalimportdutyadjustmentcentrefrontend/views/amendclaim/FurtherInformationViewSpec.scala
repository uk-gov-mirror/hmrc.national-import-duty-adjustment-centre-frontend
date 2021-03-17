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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.amend.FurtherInformationFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.FurtherInformation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendclaim.FurtherInformationView

class FurtherInformationViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[FurtherInformationView]
  private val form = new FurtherInformationFormProvider().apply()

  private def view(form: Form[FurtherInformation] = form): Document = page(form, navigatorBack)

  "FurtherInformationPage on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("further_information.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("further_information.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have label for further information" in {
      view().getElementsByAttributeValue("for", "furtherInformation") must containMessage("further_information.title")
    }

    "have 'Continue' button" in {
      view().getElementById("nidac-continue") must includeMessage("site.continue")
    }

  }

  "FurtherInformationPage on filled form" should {

    "have populated fields" in {
      val filledView = view(form.fill(furtherInformationAnswer))

      filledView.getElementById("furtherInformation").text() mustBe furtherInformationAnswer.info
    }

    "display error when" when {

      "claim reason missing" in {
        val errorView = view(form.bind(Map("furtherInformation" -> "")))
        errorView must haveFieldError("furtherInformation", "further_information.error.required")
        errorView must havePageError("further_information.error.required")
      }
    }
  }
}
