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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
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
    data.getCreateAnswers map { answers =>
      answers.claimantAddress match {
        case address if address.nonEmpty =>
          val preparedForm = answers.claimantAddress.fold(form)(form.fill)
          Ok(addressView(preparedForm, answers, backLink(answers)))
        case _ =>
          Redirect(controllers.makeclaim.routes.AddressController.onChange())
      }
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers map { answers =>
      Redirect(nextPage(answers))
    }
  }

  def onChange(): Action[AnyContent] = identify.async { implicit request =>
    addressLookupService.initialiseJourney map {
      response => Redirect(response.redirectUrl)
    }
  }

  def onUpdate(id: String): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers flatMap { answers =>
      addressLookupService.retrieveAddress(id) flatMap { confirmedAddress =>
        val el             = extractLines(confirmedAddress.address.lines)
        val updatedAddress = Address("Contact Name Here", el._1, el._2, el._3, confirmedAddress.address.postcode)
        data.updateCreateAnswers(answers => answers.copy(claimantAddress = Some(updatedAddress))) map {
          _ => Redirect(nextPage(answers))
        }
      }
    }
  }

  // modified from the address-lookup-frontend, when it wants to split the address for manual edit
  def extractLines(lines: List[String]): (String, Option[String], String) = {
    val l1: String         = lines.lift(0).getOrElse("")
    val l2: Option[String] = if (lines.length > 2) lines.lift(1) else None
    val l3: Option[String] = if (lines.length > 3) lines.lift(2) else None
    val l4: String         = if (lines.length > 1) lines.lastOption.getOrElse("") else ""

    val combo = (l2 ++ l3).reduceOption(_ + " " + _) // Dunno if a combo is going to work here
    (l1, combo, l4)
  }

}
