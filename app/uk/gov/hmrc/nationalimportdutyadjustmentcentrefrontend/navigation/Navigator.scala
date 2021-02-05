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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{ReclaimDutyType, UserAnswers}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{FirstPage, Page, _}

private case class P(page: Page, destination: () => Call, condition: UserAnswers => Boolean)

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
    val existing      = normalRoutes(page, userAnswers)
    val reimplemented = viewFor(findNextPage(page, userAnswers))
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
    P(FirstPage, controllers.routes.StartController.start, always),
    P(ClaimTypePage, controllers.makeclaim.routes.ClaimTypeController.onPageLoad, always),
    P(EntryDetailsPage, controllers.makeclaim.routes.EntryDetailsController.onPageLoad, always),
    P(ItemNumbersPage, controllers.makeclaim.routes.ItemNumbersController.onPageLoad, always),
    P(ReclaimDutyTypePage, controllers.makeclaim.routes.ReclaimDutyTypeController.onPageLoad, always),
    P(
      CustomsDutyRepaymentPage,
      controllers.makeclaim.routes.DutyRepaymentController.onPageLoadCustomsDuty,
      hasDutyType(ReclaimDutyType.Customs)
    ),
    P(
      ImportVatRepaymentPage,
      controllers.makeclaim.routes.DutyRepaymentController.onPageLoadImportVat,
      hasDutyType(ReclaimDutyType.Vat)
    ),
    P(
      OtherDutyRepaymentPage,
      controllers.makeclaim.routes.DutyRepaymentController.onPageLoadOtherDuty,
      hasDutyType(ReclaimDutyType.Other)
    ),
    P(UploadPage, controllers.makeclaim.routes.UploadFormController.onPageLoad, always),
    P(ContactDetailsPage, controllers.makeclaim.routes.ContactDetailsController.onPageLoad, always),
    P(AddressPage, controllers.makeclaim.routes.AddressController.onPageLoad, always),
    P(BankDetailsPage, controllers.makeclaim.routes.BankDetailsController.onPageLoad, always),
    P(CheckYourAnswersPage, controllers.makeclaim.routes.CheckYourAnswersController.onPageLoad, always),
    P(ConfirmationPage, controllers.makeclaim.routes.ConfirmationController.onPageLoad, always)
  )

  private val pagesInOrder: Seq[Page] = pageOrder.map(_.page)

  private def findNextPage(currentPage: Page, userAnswers: UserAnswers): Page =
    after(currentPage)
      .find(p => p.condition(userAnswers))
      .getOrElse(
        throw new IllegalStateException(s"Could not find next page for: $currentPage")
      ) // TODO improve handling/messaging here?
      .page

  private def viewFor(page: Page): Call =
    pageOrder
      .find(_.page == page)
      .getOrElse(throw new IllegalStateException(s"Unknown page: $page")) // TODO improve handling/messaging here?
      .destination()

  // TODO use this to generate href for <a class="govuk-back-link">Back</a> links
  def previousPage(currentPage: Page, userAnswers: UserAnswers): Call = {
    val backTo: Page = before(currentPage)
      .find(candidate => findNextPage(candidate.page, userAnswers) == currentPage)
      .map(_.page)
      .getOrElse(throw new IllegalStateException("Maybe this should return an optional and we don't render a backlink"))

    viewFor(backTo)
  }

  private def before(page: Page): Seq[P] = pageOrder.take(pagesInOrder.indexOf(page))
  private def after(page: Page): Seq[P]  = pageOrder.drop(pagesInOrder.indexOf(page) + 1)
}
