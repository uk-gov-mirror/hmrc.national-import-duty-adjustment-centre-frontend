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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{Claim, RepresentationType}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.viewmodels.MessageKey
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.CheckYourAnswersView

class CheckYourAnswersViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[CheckYourAnswersView]

  private val completeClaim                                = Claim(completeAnswers)
  private def view(claim: Claim = completeClaim): Document = page(claim, navigatorBack)

  "CheckYourAnswersPage" should {

    "have correct title" in {
      view().title() must startWith(messages("check_answers.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("check_answers.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have claim section" which {

      val claimSection = view().getElementById("claim_section")

      "contains valid claim type" in {

        val claimTypeRow = claimSection.getElementsByClass("claim_type_row")

        claimTypeRow must haveSummaryKey(messages("check_answers.claim.claimType"))
        claimTypeRow must haveSummaryValue(MessageKey.apply("claim_type", claimTypeAnswer.toString))
      }

      "contains valid upload" in {

        val uploadRow = claimSection.getElementsByClass("upload_row")

        uploadRow must haveSummaryKey(messages("check_answers.claim.uploaded"))
        uploadRow must haveSummaryValue(uploadAnswer.fileName)
      }

    }

    "have repayment section" which {

      val repaymentSection = view().getElementById("repayment_section")

      "contains valid reclaim duty type" in {

        val reclaimDutyTypeRow = repaymentSection.getElementsByClass("reclaim_duty_type_row")

        reclaimDutyTypeRow must haveSummaryKey(messages("check_answers.repayment.reclaimDutyType"))
        reclaimDutyTypeRow must haveSummaryValue(
          reclaimDutyTypesAnswer.map(
            value => MessageKey.apply("check_answers.repayment.reclaimDutyType", value.toString)
          ).mkString(", ")
        )
      }
    }

    "not have importer details when claimant is importer" in {
      view(completeClaim.copy(representationType = RepresentationType.Importer)).getElementById(
        "importer_section"
      ) must notBePresent
    }

    "have import details section" which {
      val importerSection = view().getElementById("importer_section")

      "contains does import have Eori" in {
        val eoriRow = importerSection.getElementsByClass("importer_has_eori_row")
        eoriRow must haveSummaryKey(messages("check_answers.importer.hasEori"))
        eoriRow must haveSummaryValue(MessageKey.apply("check_answers.importer.hasEori", true.toString))
      }

      "contains Eori number when importer has EORI" in {
        val eoriRow = importerSection.getElementsByClass("importer_eori_row")

        eoriRow must haveSummaryKey(messages("check_answers.importer.eori"))
        eoriRow must haveSummaryValue(importerEoriNumberAnswer.number)
      }

      "does not contains Eori number when importer does not have EORI" in {
        val importerSection = view(completeClaim.copy(importerEoriNumber = None)).getElementById("importer_section")

        val eoriRow = importerSection.getElementsByClass("importer_eori_row")
        eoriRow must beEmpty
      }

      "contains importer contact details" in {
        val eoriRow = importerSection.getElementsByClass("importer_contact_details_row")

        eoriRow must haveSummaryKey(messages("check_answers.importer.contactDetails"))
        eoriRow must haveSummaryValue(
          Seq(
            importerContactDetailsAnswer.name,
            importerContactDetailsAnswer.addressLine1,
            importerContactDetailsAnswer.addressLine2.getOrElse(""),
            importerContactDetailsAnswer.city,
            importerContactDetailsAnswer.postCode,
            importerContactDetailsAnswer.emailAddress,
            importerContactDetailsAnswer.telephoneNumber
          ).mkString(" ")
        )
      }
    }

    "have 'Submit' button" in {
      view().getElementById("submit") must includeMessage("check_answers.submit")
    }

  }
}
