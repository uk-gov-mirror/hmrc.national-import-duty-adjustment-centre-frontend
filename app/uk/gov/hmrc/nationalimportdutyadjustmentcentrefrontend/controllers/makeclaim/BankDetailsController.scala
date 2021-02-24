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
import play.api.data.FormError
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.BankDetailsFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.bars.BARSResult
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{BankDetails, CreateAnswers, RepayTo}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.Navigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{BankDetailsPage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.{BankAccountReputationService, CacheDataService}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.BankDetailsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.ExecutionContext

@Singleton
class BankDetailsController @Inject() (
  identify: IdentifierAction,
  data: CacheDataService,
  bankAccountReputationService: BankAccountReputationService,
  formProvider: BankDetailsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  val navigator: Navigator,
  bankDetailsView: BankDetailsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Navigation {

  override val page: Page = BankDetailsPage

  private val form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers map { answers =>
      val preparedForm = answers.bankDetails.fold(form)(form.fill)
      Ok(bankDetailsView(preparedForm, importersBankDetails(answers), backLink(answers)))
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors =>
        data.getCreateAnswers map { answers =>
          BadRequest(bankDetailsView(formWithErrors, importersBankDetails(answers), backLink(answers)))
        },
      value =>
        bankAccountReputationService.validate(value) flatMap {
          case barsResult if barsResult.isValid =>
            data.updateCreateAnswers(answers => answers.copy(bankDetails = Some(value))) map {
              updatedAnswers => Redirect(nextPage(updatedAnswers))
            }
          case barsResult => processBarsFailure(value, barsResult)
        }
    )
  }

  private def processBarsFailure(bankDetails: BankDetails, barsResult: BARSResult)(implicit
    request: IdentifierRequest[_]
  ) = {

    val formWithErrors = barsResult match {
      case bars if bars.accountNumberWithSortCodeIsValid != "yes" =>
        form.fill(bankDetails).copy(errors =
          Seq(FormError("accountNumber", "bankDetails.bars.validation.modCheckFailed"))
        )
      case _ => form.fill(bankDetails).copy(errors = Seq(FormError("", "bankDetails.bars.validation.failed")))
    }
    data.getCreateAnswers map { answers =>
      BadRequest(bankDetailsView(formWithErrors, importersBankDetails(answers), backLink(answers)))
    }

  }

  private def importersBankDetails: CreateAnswers => Boolean = (answers: CreateAnswers) =>
    answers.isRepresentative && answers.repayTo.contains(RepayTo.Importer)

}
