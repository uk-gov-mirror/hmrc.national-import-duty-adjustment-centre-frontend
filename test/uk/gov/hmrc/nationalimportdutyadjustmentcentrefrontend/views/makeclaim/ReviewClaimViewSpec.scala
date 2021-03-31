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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitViewSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{
  CreateAnswers,
  CreateClaimReceipt,
  CreateClaimResponse,
  CreateClaimResult
}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ReviewClaimView

import scala.util.Random

class ReviewClaimViewSpec extends UnitViewSpec {

  private val page = instanceOf[ReviewClaimView]

  private val claimReference = Random.alphanumeric.take(16).mkString

  private val receipt = CreateClaimReceipt(
    CreateClaimResponse("id", result = Some(CreateClaimResult(claimReference, Seq.empty))),
    CreateAnswers()
  )

  private val view: Document = page(receipt)

  "ReviewClaimView" should {

    "have correct title" in {
      view.title() must startWith(messages("create.claim.summary.title"))
    }

    "have correct heading" in {
      view.getElementsByTag("h1") must containMessage("create.claim.summary.title")
    }

    "not have back link" in {
      view.getElementsByClass("govuk-back-link") must beEmpty
    }
  }
}
