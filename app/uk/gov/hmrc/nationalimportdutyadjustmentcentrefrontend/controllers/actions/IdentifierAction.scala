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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.actions

import com.google.inject.Inject
import play.api.mvc.Results._
import play.api.mvc._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.v2.Retrievals.{allEnrolments, internalId}
import uk.gov.hmrc.http.{HeaderCarrier, UnauthorizedException}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers.routes
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.EoriNumber
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.requests.IdentifierRequest
import uk.gov.hmrc.play.HeaderCarrierConverter

import scala.concurrent.{ExecutionContext, Future}

trait IdentifierAction
    extends ActionBuilder[IdentifierRequest, AnyContent] with ActionFunction[Request, IdentifierRequest]

class AuthenticatedIdentifierAction @Inject() (
  override val authConnector: AuthConnector,
  config: AppConfig,
  val parser: BodyParsers.Default
)(implicit val executionContext: ExecutionContext)
    extends IdentifierAction with AuthorisedFunctions {

  override def invokeBlock[A](request: Request[A], block: IdentifierRequest[A] => Future[Result]): Future[Result] = {

    implicit val hc: HeaderCarrier =
      HeaderCarrierConverter.fromHeadersAndSession(request.headers, Some(request.session))

    authorised().retrieve(internalId and allEnrolments) {

      case userInternalId ~ allUsersEnrolments =>

        val enrolment = allUsersEnrolments.getEnrolment("HMRC-CTS-ORG").getOrElse(
          throw InsufficientEnrolments("User does not have enroment HMRC-CTS-ORG")
        )

        val eoriNumber = enrolment.getIdentifier("EORINumber").map(_.value).getOrElse(
          throw InsufficientEnrolments("Enrolment HMRC-CTS-ORG does not have an associated EORI number")
        )

        userInternalId.map(internalId => block(IdentifierRequest(request, internalId, EoriNumber(eoriNumber)))).getOrElse(
          throw UnauthorizedException("Unable to retrieve internalId")
        )

    } recover {
      case _: InsufficientEnrolments =>
        Redirect("http://localhost:6750/customs-enrolment-services/nidac/subscribe")
      case _: NoActiveSession =>
        Redirect(config.loginUrl, Map("continue" -> Seq(config.loginContinueUrl)))
      case _: AuthorisationException =>
        Redirect(routes.UnauthorisedController.onPageLoad())
    }
  }

}
