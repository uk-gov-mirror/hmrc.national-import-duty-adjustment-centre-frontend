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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.makeclaim

import javax.inject.{Inject, Singleton}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.DutyPaidFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ReclaimDutyType.{Customs, Other, Vat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.{CreateAnswers, DutyPaid, ReclaimDutyType}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.Navigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.CacheDataService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.DutyRepaymentView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

@Singleton
class DutyRepaymentController @Inject() (
  identify: IdentifierAction,
  data: CacheDataService,
  formProvider: DutyPaidFormProvider,
  val controllerComponents: MessagesControllerComponents,
  navigator: Navigator,
  repaymentView: DutyRepaymentView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  def onPageLoadCustomsDuty(): Action[AnyContent] = onPageLoad(Customs)
  def onPageLoadImportVat(): Action[AnyContent]   = onPageLoad(Vat)
  def onPageLoadOtherDuty(): Action[AnyContent]   = onPageLoad(Other)

  private def onPageLoad(dutyType: ReclaimDutyType): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers map { answers =>
      val preparedForm = answers.reclaimDutyPayments.get(dutyType).fold(form)(form.fill)
      Ok(page(dutyType, preparedForm, answers))
    }
  }

  def onSubmitCustomsDuty(): Action[AnyContent] = onSubmit(Customs)
  def onSubmitImportVat(): Action[AnyContent]   = onSubmit(Vat)
  def onSubmitOtherDuty(): Action[AnyContent]   = onSubmit(Other)

  private def onSubmit(dutyType: ReclaimDutyType): Action[AnyContent] = identify.async { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors => data.getCreateAnswers map { answers => BadRequest(page(dutyType, formWithErrors, answers)) },
      value =>
        data.updateCreateAnswers(
          answers => answers.copy(reclaimDutyPayments = answers.reclaimDutyPayments.updated(dutyType, value))
        ) map {
          updatedAnswers => Redirect(navigator.nextPage(currentPage(dutyType), updatedAnswers))
        }
    )
  }

  private def currentPage(dutyType: ReclaimDutyType) = dutyType match {
    case Customs => CustomsDutyRepaymentPage
    case Vat     => ImportVatRepaymentPage
    case Other   => OtherDutyRepaymentPage
    case _       => FirstPage
  }

  private def page(dutyType: ReclaimDutyType, form: Form[DutyPaid], answers: CreateAnswers)(implicit
    request: IdentifierRequest[_]
  ) = {
    val backLink = navigator.previousPage(currentPage(dutyType), answers)
    dutyType match {
      case Customs =>
        repaymentView(form, routes.DutyRepaymentController.onSubmitCustomsDuty(), "customsDutyPaid", backLink)
      case Vat   => repaymentView(form, routes.DutyRepaymentController.onSubmitImportVat(), "importVatPaid", backLink)
      case Other => repaymentView(form, routes.DutyRepaymentController.onSubmitOtherDuty(), "otherDutyPaid", backLink)
    }
  }

}
