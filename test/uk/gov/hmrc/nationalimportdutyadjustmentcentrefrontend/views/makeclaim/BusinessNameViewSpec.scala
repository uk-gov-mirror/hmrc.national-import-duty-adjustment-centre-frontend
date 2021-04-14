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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.{
  AddressFormProvider,
  BusinessNameFormProvider
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.BusinessName
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.BusinessNameView

class BusinessNameViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[BusinessNameView]
  private val form = new BusinessNameFormProvider().apply()

  private def view(form: Form[BusinessName] = form): Document = page(form, navigatorBack)

  "Business Name page on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("businessName.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("businessName.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have 'Continue' button" in {
      view().getElementById("nidac-continue") must includeMessage("site.continue")
    }

  }

  "AddressPage on filled form" should {

    "have populated fields" in {
      val filledView = view(form.fill(businessNameAnswer))

      filledView.getElementById("name") must haveValue(businessNameAnswer.name)
    }

    "display error when " when {

      "business name is missing" in {
        val errorView = view(form.bind(Map("name" -> "")))
        errorView must haveFieldError("name", "businessName.name.error.required")
        errorView must havePageError("businessName.name.error.required")
      }

    }

  }
}
