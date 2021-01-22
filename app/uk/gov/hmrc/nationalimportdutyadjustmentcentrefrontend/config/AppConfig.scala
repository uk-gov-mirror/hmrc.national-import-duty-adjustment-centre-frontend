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

@Singleton
class AppConfig @Inject() (config: Configuration, servicesConfig: ServicesConfig) {

  case class Upscan(callbackBase: String, maxFileSizeMb: Int, approvedFileTypes: String, approvedFileExtensions: String)

  val welshLanguageSupportEnabled: Boolean = config
    .getOptional[Boolean]("features.welsh-language-support")
    .getOrElse(false)

  val en: String            = "en"
  val cy: String            = "cy"
  val defaultLanguage: Lang = Lang(en)

  lazy val loginUrl: String         = loadConfig("urls.login")
  lazy val loginContinueUrl: String = loadConfig("urls.loginContinue")
  lazy val signOutUrl: String       = loadConfig("urls.signout")

  val nidacServiceBaseUrl: String = servicesConfig.baseUrl("national-import-duty-adjustment-centre")
  val upscanInitiateV2Url: String = servicesConfig.baseUrl("upscan-initiate") + "/upscan/v2/initiate"

  val upscan: Upscan = Upscan(
    callbackBase = loadConfig("upscan.callback-base"),
    maxFileSizeMb = config.get[Int]("upscan.max-file-size-mb"),
    approvedFileExtensions = loadConfig("upscan.approved-file-extensions"),
    approvedFileTypes = loadConfig("upscan.approved-file-types")
  )

  private def loadConfig(key: String) =
    config.getOptional[String](key).getOrElse(throw new Exception(s"Missing configuration key: $key"))

}
