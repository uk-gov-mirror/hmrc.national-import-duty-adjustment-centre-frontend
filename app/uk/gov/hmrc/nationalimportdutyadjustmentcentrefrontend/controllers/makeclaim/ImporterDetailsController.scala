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
import play.api.i18n.I18nSupport
import play.api.mvc._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.Navigation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions.IdentifierAction
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create.ImporterDetailsFormProvider
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.CreateAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation.CreateNavigator
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{ImporterContactDetailsPage, Page}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.services.CacheDataService
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.makeclaim.ImporterDetailsView
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Implicits.SanitizedString

import scala.concurrent.ExecutionContext

@Singleton
class ImporterDetailsController @Inject() (
  identify: IdentifierAction,
  data: CacheDataService,
  formProvider: ImporterDetailsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  val navigator: CreateNavigator,
  detailsView: ImporterDetailsView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Navigation[CreateAnswers] {

  override val page: Page = ImporterContactDetailsPage

  private val form = formProvider()

  def onPageLoad(): Action[AnyContent] = identify.async { implicit request =>
    data.getCreateAnswers map { answers =>
      val preparedForm = answers.importerContactDetails.fold(form)(form.fill)
      Ok(detailsView(preparedForm, backLink(answers)))
    }
  }

  def onSubmit(): Action[AnyContent] = identify.async { implicit request =>
    val cleanedInput = cleanPostCode(request.body.asFormUrlEncoded.get)

    form.bindFromRequest(cleanedInput).fold(
      formWithErrors =>
        data.getCreateAnswers map { answers => BadRequest(detailsView(formWithErrors, backLink(answers))) },
      value =>
        data.updateCreateAnswers(answers => answers.copy(importerContactDetails = Some(value))) map {
          updatedAnswers => Redirect(nextPage(updatedAnswers))
        }
    )
  }

  def cleanPostCode(data: Map[String, Seq[String]]): Map[String, Seq[String]] =
    data.map {
      case (key, values) =>
        if (key == "postcode") (key, values.map(_.stripExternalAndReduceInternalSpaces()))
        else (key, values)
    }

}
