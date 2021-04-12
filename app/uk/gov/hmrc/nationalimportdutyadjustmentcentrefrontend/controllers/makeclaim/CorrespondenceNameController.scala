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
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.{CorrespondenceNameFormProvider}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.CreateAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.CreateNavigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{CorrespondenceNamePage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.CacheDataService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.CorrespondenceNameView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CorrespondenceNameController @Inject() (
                                           identify: IdentifierAction,
                                           data: CacheDataService,
                                           formProvider: CorrespondenceNameFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           val navigator: CreateNavigator,
                                           correspondenceNameView: CorrespondenceNameView
                                         )(implicit ec: ExecutionContext)
  extends FrontendBaseController with I18nSupport with Navigation[CreateAnswers] {

  override val page: Page = CorrespondenceNamePage

  private val form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers map { answers =>
      val preparedForm = answers.correspondenceName.fold(form)(form.fill)
      Ok(correspondenceNameView(preparedForm, backLink(answers)))
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    form.bindFromRequest().fold(
      formWithErrors =>
        data.getCreateAnswers map { answers => BadRequest(correspondenceNameView(formWithErrors, backLink(answers))) },
      value =>
        data.updateCreateAnswers(answers => answers.copy(correspondenceName = Some(value))) map {
          updatedAnswers => Redirect(nextPage(updatedAnswers))
        }
    )
  }

}
