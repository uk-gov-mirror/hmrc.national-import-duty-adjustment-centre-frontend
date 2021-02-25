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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.ImporterDetailsFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ImporterContactDetails
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ImporterDetailsView

class ImporterDetailsViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[ImporterDetailsView]
  private val form = new ImporterDetailsFormProvider().apply()

  private def view(form: Form[ImporterContactDetails] = form): Document = page(form, navigatorBack)

  "ImporterDetailsView on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("importer-details.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("importer-details.title")
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

    "have label for email" in {
      view().getElementsByAttributeValue("for", "emailAddress") must containMessage(
        "contactDetails.emailAddress.heading"
      )
    }

    "have label for phone number" in {
      view().getElementsByAttributeValue("for", "telephoneNumber") must containMessage(
        "contactDetails.telephoneNumber.heading"
      )
    }

    "have 'Continue' button" in {
      view().getElementById("submit") must includeMessage("site.continue")
    }

  }

  "AddressPage on filled form" should {

    "have populated fields" in {
      val filledView = view(form.fill(importerContactDetailsAnswer))

      filledView.getElementById("name") must haveValue(importerContactDetailsAnswer.name)
      filledView.getElementById("addressLine1") must haveValue(importerContactDetailsAnswer.addressLine1)
      filledView.getElementById("addressLine2") must haveValue(importerContactDetailsAnswer.addressLine2.getOrElse(""))
      filledView.getElementById("city") must haveValue(importerContactDetailsAnswer.city)
      filledView.getElementById("postcode") must haveValue(importerContactDetailsAnswer.postCode)
      filledView.getElementById("emailAddress") must haveValue(importerContactDetailsAnswer.emailAddress)
      filledView.getElementById("telephoneNumber") must haveValue(importerContactDetailsAnswer.telephoneNumber)
    }

    "display error" when {

      val missingView = view(form.bind(Map("" -> "")))

      "name missing" in {
        missingView must haveFieldError("name", "address.name.error.required")
      }

      "line 1 missing" in {
        missingView must haveFieldError("addressLine1", "address.line1.error.required")
      }

      "city missing" in {
        missingView must haveFieldError("city", "address.city.error.required")
      }

      "postcode" in {
        missingView must haveFieldError("postcode", "address.postcode.error.required")
      }

      "email" in {
        missingView must haveFieldError("emailAddress", "contactDetails.emailAddress.error.required")
      }

      "telephone number" in {
        missingView must haveFieldError("telephoneNumber", "contactDetails.telephoneNumber.error.required")
      }

    }

  }
}
