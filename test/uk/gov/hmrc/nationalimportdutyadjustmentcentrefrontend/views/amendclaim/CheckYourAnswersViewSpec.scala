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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitViewSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendClaim
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.viewmodels.MessageKey
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.amendclaim.CheckYourAnswersView

class CheckYourAnswersViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[CheckYourAnswersView]

  private val completeClaim                                     = AmendClaim(completeAmendAnswers)
  private def view(claim: AmendClaim = completeClaim): Document = page(claim, navigatorBack)

  "CheckYourAnswersPage" should {

    "have correct title" in {
      view().title() must startWith(messages("amend.check_answers.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("amend.check_answers.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have important details section" which {
      val section = view().getElementById("important_info_section")

      "contains case reference number" in {
        val caseRow = section.getElementsByClass("case_reference_row")

        caseRow must haveSummaryKey(messages("amend.check_answers.information.caseReference"))
        caseRow must haveSummaryValue(caseReferenceAnswer.number)

        caseRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("amend.check_answers.information.caseReference.accessible")}"
        )
      }

      "contains do you want to attach docs?" in {

        val attachDocsRow = section.getElementsByClass("has_supporting_documents_row")

        attachDocsRow must haveSummaryKey(messages("amend.check_answers.information.attach_more_documents"))
        attachDocsRow must haveSummaryValue(
          MessageKey.apply("amend.check_answers.information.attach_more_documents", hasMoreDocumentsAnswer.toString)
        )

        attachDocsRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("amend.check_answers.information.attach_more_documents.accessible")}"
        )
      }

      "contains uploaded documents" in {

        val uploadRow = section.getElementsByClass("upload_row")

        uploadRow must haveSummaryKey(messages("amend.check_answers.information.uploadedDocuments"))
        uploadRow must haveSummaryValue(s"${uploadAnswer.fileName} ${uploadAnswer2.fileName}")

        uploadRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("amend.check_answers.information.uploadedDocuments.accessible")}"
        )
      }

      "contains further information" in {

        val furtherInfoRow = section.getElementsByClass("further_information_row")

        furtherInfoRow must haveSummaryKey(messages("amend.check_answers.information.further_information"))
        furtherInfoRow must haveSummaryValue(furtherInformationAnswer.info)

        furtherInfoRow must haveSummaryChangeLinkText(
          s"${messages("site.change")} ${messages("amend.check_answers.information.further_information.accessible")}"
        )
      }
    }

    "have 'Submit' button" in {
      view().getElementById("submit") must includeMessage("check_answers.submit")
    }

  }
}
