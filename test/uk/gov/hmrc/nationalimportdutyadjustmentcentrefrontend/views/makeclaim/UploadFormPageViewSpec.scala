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

import play.api.data.FormError
import play.twirl.api.Html
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{TestData, UnitViewSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ClaimType
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ClaimType.{
  Airworthiness,
  AntiDumping,
  Preference,
  Quota
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.UploadFormPage

import scala.collection.JavaConverters._

class UploadFormPageViewSpec extends UnitViewSpec with TestData {

  private val page      = instanceOf[UploadFormPage]
  private val appConfig = instanceOf[AppConfig]

  private def view(claimType: Option[ClaimType] = None, error: Option[FormError] = None): Html =
    page(upscanInitiateResponse, claimType, error)

  "UploadFormPage" should {

    "have correct title" in {
      view().title() must startWith(messages("upload_documents.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("upload_documents.title")
    }

    "render hidden input fields" in {
      view().getElementsByAttributeValue("name", "field-hidden").size() mustBe 1
    }

    "render input file with correct file-types" in {
      view().getElementById("upload-file").attr("type") mustBe "file"
      view().getElementById("upload-file").attr("accept") mustBe appConfig.upscan.approvedFileExtensions
    }

    "display error when no choice is made" in {
      val errorView = view(error = Some(FormError("key", "error.file-upload.required")))
      errorView.getElementsByClass("govuk-error-summary__body").text() mustBe messages("error.file-upload.required")
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
    }
  }
}
