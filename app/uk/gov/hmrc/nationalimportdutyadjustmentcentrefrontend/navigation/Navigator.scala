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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation

import play.api.mvc.Call
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Answers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.Page
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.viewmodels.NavigatorBack

trait Navigator[T <: Answers] {
  protected case class P(page: Page, destination: () => Call, canAccessGiven: T => Boolean, hasAnswer: T => Boolean)

  protected val pageOrder: Seq[P]

  protected def checkYourAnswersPage: Call

  protected def pageFor: String => Option[Page]

  private lazy val reversePageOrder = pageOrder.reverse

  def gotoPage(pageName: String): Call = viewFor(pageOrder, pageFor(pageName)).getOrElse(pageOrder.head.destination())

  def firstMissingAnswer(userAnswers: T): Call =
    viewFor(pageOrder, nextPageAfterChangeFor(pageOrder, pageOrder.head.page, userAnswers)).getOrElse(
      checkYourAnswersPage
    )

  def nextPage(currentPage: Page, userAnswers: T): Call = userAnswers.changePage match {
    case None =>
      viewFor(pageOrder, nextPageFor(pageOrder, currentPage, userAnswers)).getOrElse(pageOrder.head.destination())
    case Some(_) =>
      viewFor(pageOrder, nextPageAfterChangeFor(pageOrder, currentPage, userAnswers)).getOrElse(checkYourAnswersPage)
  }

  private val jsBackLink: Call = Call("GET", "javascript:history.back()")

  def previousPage(currentPage: Page, userAnswers: T): NavigatorBack =
    if (userAnswers.changePage.isDefined)
      NavigatorBack(Some(jsBackLink))
    else
      NavigatorBack(viewFor(pageOrder, nextPageFor(reversePageOrder, currentPage, userAnswers)))

  private val nextPageFor: (Seq[P], Page, T) => Option[Page] = (pages, currentPage, userAnswers) =>
    after(pages, currentPage)
      .find(_.canAccessGiven(userAnswers))
      .map(_.page)

  protected val nextPageAfterChangeFor: (Seq[P], Page, T) => Option[Page] =
    (pages, currentPage, userAnswers) => {
      after(pages, currentPage)
        .find(p => p.canAccessGiven(userAnswers) && !p.hasAnswer(userAnswers))
        .map(_.page)
    }

  private val viewFor: (Seq[P], Option[Page]) => Option[Call] = (pages, page) =>
    page.flatMap(
      p =>
        pages
          .find(_.page == p)
          .map(_.destination())
    )

  private def after(pages: Seq[P], page: Page): Seq[P] = pages.span(_.page != page)._2 match {
    case s if s.isEmpty => Seq.empty
    case s              => s.tail
  }

}
