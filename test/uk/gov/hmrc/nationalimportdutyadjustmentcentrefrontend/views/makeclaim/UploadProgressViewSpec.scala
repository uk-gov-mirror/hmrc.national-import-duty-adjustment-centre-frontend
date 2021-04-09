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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ClaimType.Quota
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.UploadProgressView

class UploadProgressViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[UploadProgressView]

  private val view: Document = page(Seq.empty, Some(Quota), navigatorBack)

  "UploadProgressPage" should {

    "have correct title" in {
      view.title() must startWith(messages("upload_documents.title"))
    }

    "have correct heading" in {
      view.getElementsByTag("h1") must containMessage("upload_documents.title")
    }

    "have correct message " in {
      view.getElementsByClass("govuk-summary-list") must containMessage("upload_documents.status.in_progress")
    }

    "have auto refresh " in {
      view.getElementsByAttributeValue("http-equiv", "refresh").size() mustBe 1
    }

    "have back link" in {
      view must haveNavigatorBackLink(navigatorBackUrl)
    }
  }
}
