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
import play.api.mvc.Call
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitViewSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ClaimType
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ClaimType.{
  AccountSales,
  Airworthiness,
  AntiDumping,
  Preference,
  Quota
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.RequiredDocumentsView

import scala.collection.JavaConverters._

class RequiredDocumentsViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[RequiredDocumentsView]

  private def view(claimType: Option[ClaimType] = None, nextPage: Call = Call("GET", "/next")): Document =
    page(claimType, nextPage, navigatorBack)

  "RequiredDocumentsView" should {

    "have correct title" in {
      view().title() must startWith(messages("upload_documents.required-docs.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("upload_documents.required-docs.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have correct document types" when {

      def documentTypes(claimType: ClaimType) =
        view(Some(claimType)).getElementById("document-type-list").children().eachText().asScala

      "claim type is Preference" in {
        val types = documentTypes(Preference)
        types mustBe List("C88", "commercial invoice", "E2", "preference certificate (EUR, ATR or other)")
      }

      "claim type is Quota" in {
        val types = documentTypes(Quota)
        types mustBe List("C88", "commercial invoice", "E2", "preference certificate (EUR, ATR or other)")
      }

      "claim type is Airworthiness" in {
        val types = documentTypes(Airworthiness)
        types mustBe List("air worthiness certificate", "C88", "commercial invoice", "E2")
      }

      "claim type is AntiDumping" in {
        val types = documentTypes(AntiDumping)
        types mustBe List("C88", "commercial invoice", "E2")
      }

      "claim type is AccountSales" in {
        val types = documentTypes(AccountSales)
        types mustBe List("C88", "certificate of origin", "commercial invoice", "E2")
      }
    }
  }
}
