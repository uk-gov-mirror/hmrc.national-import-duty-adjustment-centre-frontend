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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.ItemNumbersFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ItemNumbers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ItemNumbersView

class ItemNumbersViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[ItemNumbersView]
  private val form = new ItemNumbersFormProvider().apply()

  private def view(form: Form[ItemNumbers] = form): Document = page(form, navigatorBack)

  "ItemNumbersPage on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("itemNumbers.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("itemNumbers.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have label for item numbers" in {
      view().getElementsByAttributeValue("for", "itemNumbers") must containMessage("itemNumbers.title")
    }

    "have 'Continue' button" in {
      view().getElementById("submit") must includeMessage("site.continue")
    }

  }

  "ItemNumbersPage on filled form" should {

    "have populated fields" in {
      val filledView = view(form.fill(itemNumbersAnswer))

      filledView.getElementById("itemNumbers") must haveValue(itemNumbersAnswer.numbers)
    }

    "display error when " when {

      "item numbers missing" in {
        view(form.bind(Map("itemNumbers" -> ""))) must haveFieldError("itemNumbers", "itemNumbers.error.required")
      }

    }

  }
}
