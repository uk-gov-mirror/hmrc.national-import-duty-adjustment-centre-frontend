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

import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.YesNoFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.Navigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{ImporterHasEoriPage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.CacheDataService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ImporterHasEoriView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class ImporterHasEoriController @Inject()(
                                           identify: IdentifierAction,
                                           data: CacheDataService,
                                           formProvider: YesNoFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           val navigator: Navigator,
                                           importerHasEoriView: ImporterHasEoriView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Navigation {

  override val page: Page = ImporterHasEoriPage

  private val form = formProvider("importer.has.eori.required")

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getAnswers map { answers =>
      val preparedForm = answers.importerHasEori.fold(form)(form.fill)
      Ok(importerHasEoriView(preparedForm, backLink(answers)))
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors =>
        data.getAnswers map { answers => BadRequest(importerHasEoriView(formWithErrors, backLink(answers))) },
      value =>
        data.updateAnswers(answers => answers.copy(importerHasEori = Some(value))) map {
          updatedAnswers => Redirect(nextPage(updatedAnswers))
        }
    )
  }

}
