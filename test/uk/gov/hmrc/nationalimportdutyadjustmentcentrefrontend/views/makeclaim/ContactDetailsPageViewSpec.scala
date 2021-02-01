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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.ContactDetailsFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ContactDetails
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ContactDetailsPage

class ContactDetailsPageViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[ContactDetailsPage]
  private val form = new ContactDetailsFormProvider().apply()

  private def view(form: Form[ContactDetails] = form): Document = page(form)

  "ContactDetailsPage on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("contactDetails.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("contactDetails.title")
    }

    "have back link" in {
      view().getElementsByClass("govuk-back-link") must containMessage("site.back")
    }

    "have label for first name" in {
      view().getElementsByAttributeValue("for", "firstName") must containMessage("contactDetails.firstName.heading")
    }

    "have label for last name" in {
      view().getElementsByAttributeValue("for", "lastName") must containMessage("contactDetails.lastName.heading")
    }

    "have 'Continue' button" in {
      view().getElementById("submit") must includeMessage("site.continue")
    }

  }

  "ContactDetailsPage on filled form" should {

    "have populated fields" in {
      val filledView = view(form.fill(contactDetailsAnswer))

      filledView.getElementById("firstName") must haveValue(contactDetailsAnswer.firstName)
      filledView.getElementById("lastName") must haveValue(contactDetailsAnswer.lastName)
      filledView.getElementById("emailAddress") must haveValue(contactDetailsAnswer.emailAddress)
      filledView.getElementById("telephoneNumber") must haveValue(contactDetailsAnswer.telephoneNumber)
    }

    "display error when " when {

      val answers = Map(
        "firstName"       -> "Joe",
        "lastName"        -> "Soap",
        "emailAddress"    -> "joe@example.com",
        "telephoneNumber" -> "08001234567"
      )

      "first name missing" in {
        view(form.bind(answers - "firstName")) must haveFieldError(
          "firstName",
          "contactDetails.firstName.error.required"
        )
      }

      "last name missing" in {
        view(form.bind(answers - "lastName")) must haveFieldError("lastName", "contactDetails.lastName.error.required")
      }

      "email invalid" in {
        view(form.bind(answers + ("emailAddress" -> "invalid"))) must haveFieldError(
          "emailAddress",
          "contactDetails.emailAddress.error.invalid"
        )
      }

      "telephone number missing" in {
        view(form.bind(answers - "telephoneNumber")) must haveFieldError(
          "telephoneNumber",
          "contactDetails.telephoneNumber.error.required"
        )
      }

    }

  }
}
