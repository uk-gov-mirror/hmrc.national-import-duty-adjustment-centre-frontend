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

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.{makeclaim, routes}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{ReclaimDutyType, UserAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{FirstPage, Page, _}

protected case class P(page: Page, destination: () => Call, canAccessGiven: UserAnswers => Boolean)

@Singleton
class Navigator @Inject() () extends Conditions with Ordering {

  private val pageOrder: Seq[P] = Seq(
    P(FirstPage, routes.StartController.start, always),
    P(ClaimTypePage, makeclaim.routes.ClaimTypeController.onPageLoad, always),
    P(EntryDetailsPage, makeclaim.routes.EntryDetailsController.onPageLoad, always),
    P(ItemNumbersPage, makeclaim.routes.ItemNumbersController.onPageLoad, always),
    P(ReclaimDutyTypePage, makeclaim.routes.ReclaimDutyTypeController.onPageLoad, always),
    P(CustomsDutyRepaymentPage, makeclaim.routes.DutyRepaymentController.onPageLoadCustomsDuty, hasDutyType(Customs)),
    P(ImportVatRepaymentPage, makeclaim.routes.DutyRepaymentController.onPageLoadImportVat, hasDutyType(Vat)),
    P(OtherDutyRepaymentPage, makeclaim.routes.DutyRepaymentController.onPageLoadOtherDuty, hasDutyType(Other)),
    P(UploadSummaryPage, makeclaim.routes.UploadFormSummaryController.onPageLoad, always),
    P(ContactDetailsPage, makeclaim.routes.ContactDetailsController.onPageLoad, always),
    P(AddressPage, makeclaim.routes.AddressController.onPageLoad, always),
    P(BankDetailsPage, makeclaim.routes.BankDetailsController.onPageLoad, always),
    P(CheckYourAnswersPage, makeclaim.routes.CheckYourAnswersController.onPageLoad, always),
    P(ConfirmationPage, makeclaim.routes.ConfirmationController.onPageLoad, always)
  )

  private val reversePageOrder = pageOrder.reverse

  def nextPage(currentPage: Page, userAnswers: UserAnswers): Call =
    viewFor(pageOrder, nextPageFor(pageOrder, currentPage, userAnswers)).getOrElse(
      throw new IllegalStateException(s"No page after $currentPage")
    )

  // TODO use this to generate href for <a class="govuk-back-link">Back</a> links
  def previousPage(currentPage: Page, userAnswers: UserAnswers): Option[Call] =
    viewFor(pageOrder, nextPageFor(reversePageOrder, currentPage, userAnswers))

}

protected trait Conditions {
  protected val always: UserAnswers => Boolean = (_: UserAnswers) => true

  protected val hasDutyType: ReclaimDutyType => UserAnswers => Boolean = (dutyType: ReclaimDutyType) =>
    (userAnswers: UserAnswers) =>
      userAnswers.reclaimDutyTypes match {
        case Some(duties) => duties.contains(dutyType)
        case _            => false
      }

}

protected trait Ordering {

  protected val nextPageFor: (Seq[P], Page, UserAnswers) => Option[Page] = (pages, currentPage, userAnswers) =>
    after(pages, currentPage)
      .find(_.canAccessGiven(userAnswers))
      .map(_.page)

  protected val viewFor: (Seq[P], Option[Page]) => Option[Call] = (pages, page) =>
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
