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
import play.api.mvc.Call
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitViewSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.makeclaim.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.DutyPaidFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.DutyPaid
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.DutyRepaymentView

class DutyRepaymentViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[DutyRepaymentView]
  private val form = new DutyPaidFormProvider().apply()

  private def view(
    messagePrefix: String,
    form: Form[DutyPaid] = form,
    submit: Call = routes.DutyRepaymentController.onSubmitCustomsDuty()
  ): Document = page(form, submit, messagePrefix, navigatorBack)

  private val messagePrefixs = Seq("customsDutyPaid", "importVatPaid", "otherDutyPaid")

  "DutyRepaymentPage on empty form" should {

    "have correct title" in {
      messagePrefixs.foreach(prefix => view(prefix).title() must startWith(messages(s"$prefix.title")))
    }

    "have correct heading" in {
      messagePrefixs.foreach(prefix => view(prefix).getElementsByTag("h1") must containMessage(s"$prefix.title"))
    }

    "have back link" in {
      messagePrefixs.foreach(prefix => view(prefix) must haveNavigatorBackLink(navigatorBackUrl))
    }

    "have label for duty paid" in {
      messagePrefixs.foreach(
        prefix =>
          view(prefix).getElementsByAttributeValue("for", "actuallyPaid") must containMessage(s"$prefix.actual.label")
      )
    }

    "have label for should have paid" in {
      messagePrefixs.foreach(
        prefix =>
          view(prefix).getElementsByAttributeValue("for", "shouldPaid") must containMessage(s"$prefix.should.label")
      )
    }

    "have 'Continue' button" in {
      messagePrefixs.foreach(prefix => view(prefix).getElementById("submit") must includeMessage("site.continue"))
    }

  }

  "DutyRepaymentPage on filled form" should {

    "have populated fields" in {
      val filledView = view("customsDutyPaid", form.fill(customsDutyRepaymentAnswer))

      filledView.getElementById("actuallyPaid") must haveValue(customsDutyRepaymentAnswer.actuallyPaid)
      filledView.getElementById("shouldPaid") must haveValue(customsDutyRepaymentAnswer.shouldHavePaid)
    }

    "display error when " when {

      val answers = Map("actuallyPaid" -> "100.00", "shouldPaid" -> "89.99")

      "actuallyPaid missing" in {
        view("customsDutyPaid", form.bind(answers - "actuallyPaid")) must haveFieldError(
          "actuallyPaid",
          "dutyPaid.actual.error.required"
        )
      }

      "shouldPaid missing" in {
        view("customsDutyPaid", form.bind(answers - "shouldPaid")) must haveFieldError(
          "shouldPaid",
          "dutyPaid.should.error.required"
        )
      }

      "actuallyPaid invalid" in {
        view("customsDutyPaid", form.bind(answers + ("actuallyPaid" -> "invalid"))) must haveFieldError(
          "actuallyPaid",
          "dutyPaid.error.invalid"
        )
      }

      "shouldPaid invalid" in {
        view("customsDutyPaid", form.bind(answers + ("shouldPaid" -> "-100.123"))) must haveFieldError(
          "shouldPaid",
          "dutyPaid.error.invalid"
        )
      }

      "values are the same" in {
        view("customsDutyPaid", form.bind(Map("actuallyPaid" -> "10", "shouldPaid" -> "10.00"))) must haveFieldError(
          "shouldPaid",
          "dutyPaid.amounts.error.same"
        )
      }

      "should is more that actual" in {
        view("customsDutyPaid", form.bind(Map("actuallyPaid" -> "10", "shouldPaid" -> "10.01"))) must haveFieldError(
          "shouldPaid",
          "dutyPaid.amounts.error.greater"
        )
      }

    }

  }
}
