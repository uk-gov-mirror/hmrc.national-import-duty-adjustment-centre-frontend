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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.{makeclaim, routes}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{ReclaimDutyType, UserAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{FirstPage, Page, _}

private case class P(page: Page, destination: () => Call, canAccessGiven: UserAnswers => Boolean)

@Singleton
class Navigator @Inject() () {

  private val normalRoutes: (Page, UserAnswers) => Call = {
    case (FirstPage, _)                      => controllers.makeclaim.routes.ClaimTypeController.onPageLoad()
    case (ClaimTypePage, _)                  => controllers.makeclaim.routes.EntryDetailsController.onPageLoad()
    case (EntryDetailsPage, _)               => controllers.makeclaim.routes.ItemNumbersController.onPageLoad()
    case (ItemNumbersPage, _)                => controllers.makeclaim.routes.ReclaimDutyTypeController.onPageLoad()
    case (ReclaimDutyTypePage, answers)      => reclaimDutyTypeNextPage(answers)
    case (CustomsDutyRepaymentPage, answers) => customsDutyRepaymentNextPage(answers)
    case (ImportVatRepaymentPage, answers)   => importVatRepaymentNextPage(answers)
    case (OtherDutyRepaymentPage, answers)   => otherDutyRepaymentNextPage(answers)
    case (UploadPage, _)                     => controllers.makeclaim.routes.ContactDetailsController.onPageLoad()
    case (ContactDetailsPage, _)             => controllers.makeclaim.routes.AddressController.onPageLoad()
    case (AddressPage, _)                    => controllers.makeclaim.routes.BankDetailsController.onPageLoad()
    case (BankDetailsPage, _)                => controllers.makeclaim.routes.CheckYourAnswersController.onPageLoad()
    case _                                   => controllers.routes.StartController.start()
  }

  private def reclaimDutyTypeNextPage(answers: UserAnswers) = answers.reclaimDutyTypes match {
    case Some(duties) if duties.contains(Customs) =>
      controllers.makeclaim.routes.DutyRepaymentController.onPageLoadCustomsDuty()
    case _ => customsDutyRepaymentNextPage(answers)
  }

  private def customsDutyRepaymentNextPage(answers: UserAnswers) = answers.reclaimDutyTypes match {
    case Some(duties) if duties.contains(Vat) =>
      controllers.makeclaim.routes.DutyRepaymentController.onPageLoadImportVat()
    case _ => importVatRepaymentNextPage(answers)
  }

  private def importVatRepaymentNextPage(answers: UserAnswers) = answers.reclaimDutyTypes match {
    case Some(duties) if duties.contains(Other) =>
      controllers.makeclaim.routes.DutyRepaymentController.onPageLoadOtherDuty()
    case _ => otherDutyRepaymentNextPage(answers)
  }

  private def otherDutyRepaymentNextPage(answers: UserAnswers) =
    controllers.makeclaim.routes.UploadFormController.onPageLoad()

  def nextPage(page: Page, userAnswers: UserAnswers): Call = {
    val existing = normalRoutes(page, userAnswers)
    val reimplemented = viewFor(nextPageFor(pageOrder, page, userAnswers)).getOrElse(
      throw new IllegalStateException(s"No page after $page")
    )
    if (existing != reimplemented)
      throw new IllegalStateException("New implementation did not return same result as old one")
    reimplemented
  }

  // Alternate implementation below here

  private val always = (_: UserAnswers) => true

  private val hasDutyType = (dutyType: ReclaimDutyType) =>
    (userAnswers: UserAnswers) =>
      userAnswers.reclaimDutyTypes match {
        case Some(duties) => duties.contains(dutyType)
        case _            => false
      }

  private val pageOrder: Seq[P] = Seq(
    P(FirstPage, routes.StartController.start, always),
    P(ClaimTypePage, makeclaim.routes.ClaimTypeController.onPageLoad, always),
    P(EntryDetailsPage, makeclaim.routes.EntryDetailsController.onPageLoad, always),
    P(ItemNumbersPage, makeclaim.routes.ItemNumbersController.onPageLoad, always),
    P(ReclaimDutyTypePage, makeclaim.routes.ReclaimDutyTypeController.onPageLoad, always),
    P(CustomsDutyRepaymentPage, makeclaim.routes.DutyRepaymentController.onPageLoadCustomsDuty, hasDutyType(Customs)),
    P(ImportVatRepaymentPage, makeclaim.routes.DutyRepaymentController.onPageLoadImportVat, hasDutyType(Vat)),
    P(OtherDutyRepaymentPage, makeclaim.routes.DutyRepaymentController.onPageLoadOtherDuty, hasDutyType(Other)),
    P(UploadPage, makeclaim.routes.UploadFormController.onPageLoad, always),
    P(ContactDetailsPage, makeclaim.routes.ContactDetailsController.onPageLoad, always),
    P(AddressPage, makeclaim.routes.AddressController.onPageLoad, always),
    P(BankDetailsPage, makeclaim.routes.BankDetailsController.onPageLoad, always),
    P(CheckYourAnswersPage, makeclaim.routes.CheckYourAnswersController.onPageLoad, always),
    P(ConfirmationPage, makeclaim.routes.ConfirmationController.onPageLoad, always)
  )

  private val reversePageOrder = pageOrder.reverse

  private def nextPageFor(pages: Seq[P], currentPage: Page, userAnswers: UserAnswers): Option[Page] =
    after(pages, currentPage)
      .find(_.canAccessGiven(userAnswers))
      .map(_.page)

  private def viewFor(page: Option[Page]): Option[Call] =
    page.flatMap(
      p =>
        pageOrder
          .find(_.page == p)
          .map(_.destination())
    )

  // TODO use this to generate href for <a class="govuk-back-link">Back</a> links
  def previousPage(currentPage: Page, userAnswers: UserAnswers): Call =
    viewFor(nextPageFor(reversePageOrder, currentPage, userAnswers)).getOrElse(
      throw new IllegalStateException(s"No page before $currentPage - consider returning Option[Call]")
    )

  private def after(pages: Seq[P], page: Page): Seq[P] = pages.span(_.page != page)._2 match {
    case s if s.isEmpty => Seq.empty
    case s              => s.tail
  }

}
