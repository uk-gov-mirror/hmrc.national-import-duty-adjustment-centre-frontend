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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.AddressFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.Address
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.AddressView

class AddressViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[AddressView]
  private val form = new AddressFormProvider().apply()

  private def view(form: Form[Address] = form): Document = page(form, navigatorBack)

  "AddressPage on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("address.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("address.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have label for name" in {
      view().getElementsByAttributeValue("for", "name") must containMessage("address.name.heading")
    }

    "have label for addresss line 1" in {
      view().getElementsByAttributeValue("for", "addressLine1").text() must include(
        s"${messages("address.line1.heading")} ${messages("address.line1.hidden")}"
      )
    }

    "have label for addresss line 2" in {
      view().getElementsByAttributeValue("for", "addressLine2").text() must include(messages("address.line2.hidden"))
    }

    "have label for town or city" in {
      view().getElementsByAttributeValue("for", "city") must containMessage("address.city.heading")
    }

    "have label for postcode" in {
      view().getElementsByAttributeValue("for", "postcode") must containMessage("address.postcode.heading")
    }

    "have 'Continue' button" in {
      view().getElementById("submit") must includeMessage("site.continue")
    }

  }

  "AddressPage on filled form" should {

    "have populated fields" in {
      val filledView = view(form.fill(addressAnswer))

      filledView.getElementById("name") must haveValue(addressAnswer.name)
      filledView.getElementById("addressLine1") must haveValue(addressAnswer.addressLine1)
      filledView.getElementById("addressLine2") must haveValue(addressAnswer.addressLine2.getOrElse(""))
      filledView.getElementById("city") must haveValue(addressAnswer.city)
      filledView.getElementById("postcode") must haveValue(addressAnswer.postCode)
    }

    "display error when " when {

      val answers = Map(
        "name"         -> "Joe Soap Ltd",
        "addressLine1" -> "123 High Street",
        "addressLine2" -> "Near Bradford",
        "city"         -> "Bradford",
        "postcode"     -> "BD12CD"
      )

      "name missing" in {
        view(form.bind(answers - "name")) must haveFieldError("name", "address.name.error.required")
      }

      "line 1 missing" in {
        view(form.bind(answers - "addressLine1")) must haveFieldError("addressLine1", "address.line1.error.required")
      }

      "city missing" in {
        view(form.bind(answers - "city")) must haveFieldError("city", "address.city.error.required")
      }

      "postcode" in {
        view(form.bind(answers + ("postcode" -> "PO1!FG"))) must haveFieldError(
          "postcode",
          "address.postcode.error.invalid"
        )
      }

    }

  }
}
