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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Request
import uk.gov.hmrc.hmrcfrontend.views.Utils.urlEncode
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.play.bootstrap.frontend.filters.SessionTimeoutFilterConfig

@Singleton
class AppConfig @Inject() (
  config: Configuration,
  servicesConfig: ServicesConfig,
  sessionTimeoutConfig: SessionTimeoutFilterConfig
) {

  case class Upscan(
    callbackBase: String,
    redirectBase: String,
    maxFileSizeMb: Int,
    approvedFileTypes: String,
    approvedFileExtensions: String
  )

  val welshLanguageSupportEnabled: Boolean = config
    .getOptional[Boolean]("features.welsh-language-support")
    .getOrElse(false)

  val en: String            = "en"
  val cy: String            = "cy"
  val defaultLanguage: Lang = Lang(en)

  val loginUrl: String         = loadConfig("urls.login")
  val loginContinueUrl: String = loadConfig("urls.loginContinue")
  val signOutUrl: String       = loadConfig("urls.signout")

  private val selfBaseUrl: String = config
    .getOptional[String]("platform.frontend.host")
    .getOrElse("http://localhost:8490")

  private val serviceIdentifier = config.get[String]("contact-frontend.serviceId")

  private val authenticatedFeedbackUrl: String = config.get[String]("urls.feedback.authenticatedLink")

  private val unauthenticatedFeedbackUrl: String = config.get[String]("urls.feedback.unauthenticatedLink")

  def betaFeedBackUrl(isAuthenticated: Boolean)(implicit request: Request[_]) =
    s"${if (isAuthenticated) authenticatedFeedbackUrl
    else unauthenticatedFeedbackUrl}?service=$serviceIdentifier&backUrl=${urlEncode(s"$selfBaseUrl${request.uri}")} "

  val getEoriUrl: String = loadConfig("urls.external.getEori")

  private val sessionTimeoutSeconds: Int = sessionTimeoutConfig.timeoutDuration.getSeconds.toInt

  val timeoutDialogTimeout: Int   = sessionTimeoutSeconds
  val timeoutDialogCountdown: Int = servicesConfig.getInt("timeoutDialog.countdownSeconds")

  val mongoTimeToLiveInSeconds: Int = sessionTimeoutSeconds + 60
  val mongoReplaceIndexes: Boolean  = config.getOptional[Boolean]("mongodb.replaceIndexes").getOrElse(false)

  val nidacServiceBaseUrl: String = servicesConfig.baseUrl("national-import-duty-adjustment-centre")
  val upscanInitiateV2Url: String = servicesConfig.baseUrl("upscan-initiate") + "/upscan/v2/initiate"

  private val barsBaseUrl: String = servicesConfig.baseUrl("bank-account-reputation")

  private val addressLookupBaseUrl: String = servicesConfig.baseUrl("address-lookup-frontend")
  val addressLookupInitUrl: String         = s"$addressLookupBaseUrl${servicesConfig("address-lookup-frontend.init")}"
  val addressLookupConfirmedUrl: String    = s"$addressLookupBaseUrl${servicesConfig("address-lookup-frontend.confirmed")}"

  val yourAddressLookupCallbackUrl: String =
    s"$loginContinueUrl/create${controllers.makeclaim.routes.AddressController.onUpdate("").url}"

  val importerAddressLookupCallbackUrl: String =
    s"$loginContinueUrl/create${controllers.makeclaim.routes.ImporterDetailsController.onUpdate("").url}"

  val barsBusinessAssessUrl: String =
    s"$barsBaseUrl${servicesConfig("bank-account-reputation.businessAssess")}"

  val upscan: Upscan = Upscan(
    callbackBase = loadConfig("upscan.callback-base"),
    redirectBase = loadConfig("upscan.redirect-base"),
    maxFileSizeMb = config.get[Int]("upscan.max-file-size-mb"),
    approvedFileExtensions = loadConfig("upscan.approved-file-extensions"),
    approvedFileTypes = loadConfig("upscan.approved-file-types")
  )

  val eoriEnrolments: Seq[String] = config.get[Seq[String]]("eori.enrolments")

  private val allowListEnabled = config.get[Boolean]("eori.allowList.enabled")
  private val allowedEoris     = config.get[Seq[String]]("eori.allowList.eoris")

  val insufficientEnrolmentsUrl: Option[String] = config.getOptional[String]("eori.subscriptionJourney")

  def allowEori(eoriNumber: String): Boolean = !allowListEnabled || allowedEoris.contains(eoriNumber)

  private def servicesConfig(key: String): String = servicesConfig.getConfString(key, throwNotFound(key))

  private def loadConfig(key: String) =
    config.getOptional[String](key).getOrElse(throwNotFound(key))

  private def throwNotFound(key: String) =
    throw new Exception(s"Missing configuration key: $key")

}
