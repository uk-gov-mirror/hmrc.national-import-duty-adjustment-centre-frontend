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

  val getEoriUrl: String = loadConfig("urls.external.getEori")

  private val sessionTimeoutSeconds: Int = sessionTimeoutConfig.timeoutDuration.getSeconds.toInt

  val timeoutDialogTimeout: Int   = sessionTimeoutSeconds
  val timeoutDialogCountdown: Int = servicesConfig.getInt("timeoutDialog.countdownSeconds")

  val mongoTimeToLiveInSeconds: Int = sessionTimeoutSeconds + 60
  val mongoReplaceIndexes: Boolean  = config.getOptional[Boolean]("mongodb.replaceIndexes").getOrElse(false)

  val nidacServiceBaseUrl: String = servicesConfig.baseUrl("national-import-duty-adjustment-centre")
  val upscanInitiateV2Url: String = servicesConfig.baseUrl("upscan-initiate") + "/upscan/v2/initiate"

  private val barsBaseUrl: String = servicesConfig.baseUrl("bank-account-reputation")

  val barsBusinessAssessUrl: String =
    s"$barsBaseUrl${servicesConfig("bank-account-reputation.businessAssess")}"

  private val addressLookupBaseUrl: String = servicesConfig.baseUrl("address-lookup")
  val addressLookupInitUrl: String = s"$addressLookupBaseUrl${servicesConfig("address-lookup.init")}"
  val addressLookupConfirmedUrl: String = s"$addressLookupBaseUrl${servicesConfig("address-lookup.confirmed")}"

  val addressLookupCallbackUrl: String = "http://localhost:8490/apply-for-return-import-duty-paid-on-deposit-or-guarantee/create/your-address/update"

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

  def allowEori(eoriNumber: String): Boolean = !allowListEnabled || allowedEoris.contains(eoriNumber)

  private def servicesConfig(key: String): String = servicesConfig.getConfString(key, throwNotFound(key))

  private def loadConfig(key: String) =
    config.getOptional[String](key).getOrElse(throwNotFound(key))

  private def throwNotFound(key: String) =
    throw new Exception(s"Missing configuration key: $key")

}
