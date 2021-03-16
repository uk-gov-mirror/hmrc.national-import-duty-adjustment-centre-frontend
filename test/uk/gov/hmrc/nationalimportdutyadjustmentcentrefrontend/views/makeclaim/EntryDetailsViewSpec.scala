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

import java.time.LocalDate

import org.jsoup.nodes.Document
import play.api.data.Form
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitViewSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.EntryDetailsFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.EntryDetails
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.EntryDetailsView

class EntryDetailsViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[EntryDetailsView]
  private val form = new EntryDetailsFormProvider().apply()

  private def view(form: Form[EntryDetails] = form): Document = page(form, navigatorBack)

  "EntryDetailsPage on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("entryDetails.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("entryDetails.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have label for EPU" in {
      view().getElementsByAttributeValue("for", "entryProcessingUnit") must containMessage(
        "entryDetails.claimEpu.heading"
      )
    }

    "have label for Entry number" in {
      view().getElementsByAttributeValue("for", "entryNumber") must containMessage("entryDetails.entryNumber.heading")
    }

    "have 'Continue' button" in {
      view().getElementById("nidac-continue") must includeMessage("site.continue")
    }

  }

  "EntryDetailsPage on filled form" should {

    "have populated fields" in {
      val filledView = view(form.fill(entryDetailsAnswer))

      filledView.getElementById("entryProcessingUnit") must haveValue(entryDetailsAnswer.entryProcessingUnit)
      filledView.getElementById("entryNumber") must haveValue(entryDetailsAnswer.entryNumber)
      filledView.getElementById("entryDate") must haveValue(entryDetailsAnswer.entryDate.getDayOfMonth.toString)
      filledView.getElementById("entryDate_month") must haveValue(entryDetailsAnswer.entryDate.getMonthValue.toString)
      filledView.getElementById("entryDate_year") must haveValue(entryDetailsAnswer.entryDate.getYear.toString)
    }

    "display error when " when {

      val answers = Map(
        "entryProcessingUnit" -> "007",
        "entryNumber"         -> "654321Q",
        "entryDate"           -> "13",
        "entryDate.month"     -> "8",
        "entryDate.year"      -> "2020"
      )
      val answersWithoutDate = Map("entryProcessingUnit" -> "007", "entryNumber" -> "654321Q")

      "EPU missing" in {
        view(form.bind(answers - "entryProcessingUnit")) must haveFieldError(
          "entryProcessingUnit",
          "entryDetails.claimEpu.error.required"
        )
      }

      "EPU invalid" in {
        view(form.bind(answers + ("entryProcessingUnit" -> "1234"))) must haveFieldError(
          "entryProcessingUnit",
          "entryDetails.claimEpu.error.invalid"
        )
      }

      "Entry number missing" in {
        view(form.bind(answers - "entryNumber")) must haveFieldError(
          "entryNumber",
          "entryDetails.entryNumber.error.required"
        )
      }

      "Entry number invalid" in {
        view(form.bind(answers + ("entryNumber" -> "1234"))) must haveFieldError(
          "entryNumber",
          "entryDetails.entryNumber.error.invalid"
        )
      }

      "Entry date missing" in {
        view(form.bind(answersWithoutDate)) must haveFieldError(
          "entryDate-input",
          "entryDetails.claimEntryDate.error.required"
        )
      }

      "Entry date invalid  (other invalid dates tested in form specs) " in {
        view(form.bind(answers + ("entryDate.year" -> s"${LocalDate.now.plusYears(1).getYear}"))) must haveFieldError(
          "entryDate-input",
          "entryDetails.claimEntryDate.error.maxDate"
        )
      }

    }

  }
}
