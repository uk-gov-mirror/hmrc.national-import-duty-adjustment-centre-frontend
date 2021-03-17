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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.ClaimReasonFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ClaimReason
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ClaimReasonView

class ClaimReasonViewSpec extends UnitViewSpec with TestData {

  private val page = instanceOf[ClaimReasonView]
  private val form = new ClaimReasonFormProvider().apply()

  private def view(form: Form[ClaimReason] = form): Document = page(form, navigatorBack)

  "ClaimReasonPage on empty form" should {

    "have correct title" in {
      view().title() must startWith(messages("claim_reason.title"))
    }

    "have correct heading" in {
      view().getElementsByTag("h1") must containMessage("claim_reason.title")
    }

    "have back link" in {
      view() must haveNavigatorBackLink(navigatorBackUrl)
    }

    "have label for claim reason" in {
      view().getElementsByAttributeValue("for", "claimReason") must containMessage("claim_reason.title")
    }

    "have 'Continue' button" in {
      view().getElementById("nidac-continue") must includeMessage("site.continue")
    }

  }

  "ClaimReasonPage on filled form" should {

    "have populated fields" in {
      val filledView = view(form.fill(claimReasonAnswer))

      filledView.getElementById("claimReason").text() mustBe claimReasonAnswer.reason
    }

    "display error when" when {

      "claim reason missing" in {
        val errorView = view(form.bind(Map("claimReason" -> "")))
        errorView must haveFieldError("claimReason", "claim_reason.error.required")
        errorView must havePageError("claim_reason.error.required")
      }
    }
  }
}
