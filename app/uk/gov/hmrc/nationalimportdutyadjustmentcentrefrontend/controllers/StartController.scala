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

import javax.inject.{Inject, Singleton}
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.CreateAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.Navigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.FirstPage
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

@Singleton
class StartController @Inject() (mcc: MessagesControllerComponents, identify: IdentifierAction, navigator: Navigator)
    extends FrontendController(mcc) with I18nSupport {

  private val noAnswers = CreateAnswers()

  val start: Action[AnyContent] = identify { _ =>
    Redirect(navigator.nextPage(FirstPage, noAnswers))
  }

}
