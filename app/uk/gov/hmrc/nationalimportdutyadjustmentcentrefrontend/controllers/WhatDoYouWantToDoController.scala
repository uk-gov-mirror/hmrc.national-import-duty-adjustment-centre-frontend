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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers

import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.WhatDoYouWantToDoFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.ToDoType
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend.AmendAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.CreateAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.{AmendNavigator, CreateNavigator}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.FirstPage
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.WhatDoYouWantToDoPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class WhatDoYouWantToDoController @Inject() (
  identify: IdentifierAction,
  formProvider: WhatDoYouWantToDoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  createNavigator: CreateNavigator,
  amendNavigator: AmendNavigator,
  whatDoYouWantToDoPage: WhatDoYouWantToDoPage
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport {

  private val form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify { implicit request =>
    Ok(whatDoYouWantToDoPage(form))
  }

  def onSubmit(): Action[AnyContent] = identify { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors => BadRequest(whatDoYouWantToDoPage(formWithErrors)),
      {
        case ToDoType.NewClaim   => Redirect(createNavigator.nextPage(FirstPage, CreateAnswers()))
        case ToDoType.AmendClaim => Redirect(amendNavigator.nextPage(FirstPage, AmendAnswers()))
        case _                   => BadRequest(whatDoYouWantToDoPage(form))
      }
    )
  }

}
