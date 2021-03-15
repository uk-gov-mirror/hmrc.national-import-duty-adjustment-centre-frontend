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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitViewSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.CreateAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ReturnAmountSummaryView

class ReturnAmountSummaryViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[ReturnAmountSummaryView]

  private def view(answers: CreateAnswers = completeAnswers): Document = page(answers, navigatorBack)

  "ReturnAmountSummary page " should {

    "have correct title" in {
      view().title() must startWith(messages("returnAmountSummary.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("returnAmountSummary.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have customs duty section" which {

      val customsDutySection = view().getElementById("customs_duty_summary_section")

      "contains customs duty paid" in {

        val customsDutyPaidRow = customsDutySection.getElementsByClass("customs_paid_summary_row")

        customsDutyPaidRow must haveSummaryKey(messages("returnAmountSummary.was.paid.01"))
        customsDutyPaidRow must haveSummaryValue(s"£${customsDutyRepaymentAnswer.actuallyPaid}")

      }

      "contains customs duty should have paid" in {

        val customsDutyExpectedRow = customsDutySection.getElementsByClass("customs_expected_summary_row")

        customsDutyExpectedRow must haveSummaryKey(messages("returnAmountSummary.should.have.paid.01"))
        customsDutyExpectedRow must haveSummaryValue(s"£${customsDutyRepaymentAnswer.shouldHavePaid}")

      }

      "contains customs total" in {

        val customsDutyTotalRow = customsDutySection.getElementsByClass("customs_duty_total_row")

        customsDutyTotalRow must haveSummaryKey(messages("returnAmountSummary.duty.total.01"))
        customsDutyTotalRow must haveSummaryValue(s"£${customsDutyRepaymentAnswer.dueAmount}")

      }

    }

    "have vat section" which {

      val vatSection = view().getElementById("vat_summary_section")

      "contains vat paid" in {

        val vatPaidRow = vatSection.getElementsByClass("vat_paid_summary_row")

        vatPaidRow must haveSummaryKey(messages("returnAmountSummary.was.paid.02"))
        vatPaidRow must haveSummaryValue(s"£${importVatRepaymentAnswer.actuallyPaid}")

      }

      "contains vat should have paid" in {

        val vatExpectedRow = vatSection.getElementsByClass("vat_expected_summary_row")

        vatExpectedRow must haveSummaryKey(messages("returnAmountSummary.should.have.paid.02"))
        vatExpectedRow must haveSummaryValue(s"£${importVatRepaymentAnswer.shouldHavePaid}")

      }

      "contains vat total" in {

        val vatTotalRow = vatSection.getElementsByClass("vat_total_row")

        vatTotalRow must haveSummaryKey(messages("returnAmountSummary.duty.total.02"))
        vatTotalRow must haveSummaryValue(s"£${importVatRepaymentAnswer.dueAmount}")

      }

    }

    "have other duties section" which {

      val otherDutySection = view().getElementById("other_duties_summary_section")

      "contains other duty paid" in {

        val otherPaidRow = otherDutySection.getElementsByClass("other_paid_summary_row")

        otherPaidRow must haveSummaryKey(messages("returnAmountSummary.was.paid.03"))
        otherPaidRow must haveSummaryValue(s"£${otherDutyRepaymentAnswer.actuallyPaid}")

      }

      "contains other duty should have paid" in {

        val otherDutyExpectedRow = otherDutySection.getElementsByClass("other_expected_summary_row")

        otherDutyExpectedRow must haveSummaryKey(messages("returnAmountSummary.should.have.paid.03"))
        otherDutyExpectedRow must haveSummaryValue(s"£${otherDutyRepaymentAnswer.shouldHavePaid}")

      }

      "contains other duty total" in {

        val otherTotalRow = otherDutySection.getElementsByClass("other_total_row")

        otherTotalRow must haveSummaryKey(messages("returnAmountSummary.duty.total.03"))
        otherTotalRow must haveSummaryValue(s"£${otherDutyRepaymentAnswer.dueAmount}")

      }

    }

    "have total section" which {

      val totalSection = view().getElementById("return_amount_total_section")

      "contains total" in {

        val totalRow = totalSection.getElementsByClass("return_total_summary_row")

        totalRow must haveSummaryKey(messages("returnAmountSummary.total.label"))
        totalRow must haveSummaryValue(
          s"£${customsDutyRepaymentAnswer.dueAmount + importVatRepaymentAnswer.dueAmount + otherDutyRepaymentAnswer.dueAmount}"
        )

      }

    }

    "not have total section when only one repayment type" in {

      val vatOnlyAnswers = completeAnswers.copy(reclaimDutyPayments = Map(Vat.toString -> importVatRepaymentAnswer))

      view(vatOnlyAnswers).getElementById("return_amount_total_section") must notBePresent
    }

    "not have vat section when vat not reclaimed" in {

      val vatMissingAnswers = completeAnswers.copy(reclaimDutyPayments =
        Map(Customs.toString -> customsDutyRepaymentAnswer, Other.toString -> otherDutyRepaymentAnswer)
      )

      view(vatMissingAnswers).getElementById("vat_summary_section") must notBePresent
    }

    "not have customs duty section when customs duty not reclaimed" in {

      val customsMissingAnswers = completeAnswers.copy(reclaimDutyPayments =
        Map(Vat.toString -> importVatRepaymentAnswer, Other.toString -> otherDutyRepaymentAnswer)
      )

      view(customsMissingAnswers).getElementById("customs_duty_summary_section") must notBePresent
    }

    "not have other section when other not reclaimed" in {

      val otherMissingAnswers = completeAnswers.copy(reclaimDutyPayments =
        Map(Customs.toString -> customsDutyRepaymentAnswer, Vat.toString -> importVatRepaymentAnswer)
      )

      view(otherMissingAnswers).getElementById("other_duties_summary_section") must notBePresent
    }

    "have 'Continue' button" in {
      view().getElementById("submit") must includeMessage("site.continue")
    }

  }
}
