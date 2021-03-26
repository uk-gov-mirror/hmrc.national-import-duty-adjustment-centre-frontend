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

import play.api.Logger
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.connectors.AddressLookupConnector
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.AddressFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.CreateAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.CreateNavigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{AddressPage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.{AddressLookupService, CacheDataService}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.AddressView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.Address

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class AddressController @Inject() (
  identify: IdentifierAction,
  data: CacheDataService,
  formProvider: AddressFormProvider,
  addressLookupService: AddressLookupService,
  val controllerComponents: MessagesControllerComponents,
  val navigator: CreateNavigator,
  addressView: AddressView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Navigation[CreateAnswers] {

  override val page: Page = AddressPage

  private val form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    Logger.info(s"[AddressController][onPageLoad]")
    data.getCreateAnswers map { answers =>
      val preparedForm = answers.claimantAddress.fold(form)(form.fill)
      Ok(addressView(preparedForm, answers, backLink(answers)))
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors =>
        data.getCreateAnswers map { answers => BadRequest(addressView(formWithErrors, answers, backLink(answers))) },
      value =>
        data.updateCreateAnswers(answers => answers.copy(claimantAddress = Some(value))) map {
          updatedAnswers => Redirect(nextPage(updatedAnswers))
        }
    )
  }

  def onChange(): Action[AnyContent] = identify.async { implicit request =>
    // POST TO API
    addressLookupService.initialiseJourney map {
      response => Redirect(response.redirectUrl)
    }
  }

  def onUpdate(id: String): Action[AnyContent] = identify.async { implicit request =>
    Logger.info(s"[AddressController][onUpdate]: Calling retrieveAddress with URL - $id")
    data.getCreateAnswers flatMap { answers =>
       addressLookupService.retrieveAddress(id) flatMap {confirmedAddress =>
       val lines = confirmedAddress.address.lines
       val updatedAddress = Address(ev(lines,0), ev(lines,1), Some(ev(lines,2)), ev(lines, 3) , confirmedAddress.address.postcode)
        data.updateCreateAnswers(answers => answers.copy(claimantAddress = Some(updatedAddress))) map {
          _ => Redirect(nextPage(answers))
        }
      }
    }
  }

  def ev(input: Seq[String], index: Int): String = {
    if(input.size > index) input(index) else ""
  }
}
