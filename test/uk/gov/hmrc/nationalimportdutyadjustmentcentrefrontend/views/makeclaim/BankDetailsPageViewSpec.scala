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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.BankDetailsFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.BankDetails
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.BankDetailsPage

class BankDetailsPageViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[BankDetailsPage]
  private val form = new BankDetailsFormProvider().apply()

  private def view(form: Form[BankDetails] = form): Document = page(form)

  "BankDetailsPage on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("bankDetails.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("bankDetails.title")
    }

    "have back link" in {
      view().getElementsByClass("govuk-back-link") must containMessage("site.back")
    }

    "have label for account name" in {
      view().getElementsByAttributeValue("for", "accountName") must containMessage("bankDetails.name.heading")
    }

    "have label for sort code" in {
      view().getElementsByAttributeValue("for", "sortCode") must containMessage("bankDetails.sortCode.heading")
    }

    "have label for account number " in {
      view().getElementsByAttributeValue("for", "accountNumber") must containMessage(
        "bankDetails.accountNumber.heading"
      )
    }

    "have 'Continue' button" in {
      view().getElementById("submit") must includeMessage("site.continue")
    }

  }

  "ReclaimDutyTypePage on filled form" should {

    "have populated fields" in {
      val filledView = view(form.fill(bankDetailsAnswer))

      filledView.getElementById("accountName") must haveValue(bankDetailsAnswer.accountName)
      filledView.getElementById("sortCode") must haveValue(bankDetailsAnswer.sortCode)
      filledView.getElementById("accountNumber") must haveValue(bankDetailsAnswer.accountNumber)
    }

    "display error when " when {

      val answers = Map("accountName" -> "ACME Bank", "sortCode" -> "654321", "accountNumber" -> "87654321")

      "account name missing" in {
        view(form.bind(answers - "accountName")) must haveFieldError("accountName", "bankDetails.name.error.required")
      }

      "sort code missing" in {
        view(form.bind(answers - "sortCode")) must haveFieldError("sortCode", "bankDetails.sortCode.error.required")
      }

      "sort code invalid" in {
        view(form.bind(answers + ("sortCode" -> "1234"))) must haveFieldError(
          "sortCode",
          "bankDetails.sortCode.error.invalid"
        )
      }

      "account number missing" in {
        view(form.bind(answers - "accountNumber")) must haveFieldError(
          "accountNumber",
          "bankDetails.accountNumber.error.required"
        )
      }

      "account number invalid" in {
        view(form.bind(answers + ("accountNumber" -> "invalid"))) must haveFieldError(
          "accountNumber",
          "bankDetails.accountNumber.error.invalid"
        )
      }
    }

  }
}
