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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup

import play.api.i18n.MessagesApi
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config.AppConfig
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.addresslookup.AddressLookupRequest.Labels.Language.{AppLevelLabels, ConfirmPageLabels, EditPageLabels, LookupPageLabels, SelectPageLabels}

case class AddressLookupRequest(
  version: Int = 2,
  options: AddressLookupRequest.Options,
  labels: AddressLookupRequest.Labels
)

object AddressLookupRequest {
  implicit val format: OFormat[AddressLookupRequest] = Json.format[AddressLookupRequest]

  def apply(continueUrl: String)(implicit messagesApi: MessagesApi, config: AppConfig): AddressLookupRequest = new AddressLookupRequest(
    2,
    Options(continueUrl, false, true),
    Labels(
      AddressLookupRequest.Labels.Language(
      AppLevelLabels("navtitle", "phase Banner HTML"),
      SelectPageLabels("title", "heading", "submitLabel", "editAddressLinkText"),
      LookupPageLabels("title", "heading", "filterLabel", "postcodeLabel", "submitLabel"),
      ConfirmPageLabels("title","heading",true),
      EditPageLabels("submitLabel"))))


  case class Options(continueUrl: String, showPhaseBanner: Boolean, ukMode: Boolean)

  object Options {
    implicit val format: OFormat[Options] = Json.format[Options]
  }

  case class Labels(en: AddressLookupRequest.Labels.Language)

  object Labels {
    implicit val format: OFormat[Labels] = Json.format[Labels]

    case class Language(
      appLevelLabels: AddressLookupRequest.Labels.Language.AppLevelLabels,
      selectPageLabels: AddressLookupRequest.Labels.Language.SelectPageLabels,
      lookupPageLabels: AddressLookupRequest.Labels.Language.LookupPageLabels,
      confirmPageLabels: AddressLookupRequest.Labels.Language.ConfirmPageLabels,
      editPageLabels: AddressLookupRequest.Labels.Language.EditPageLabels
    )

    object Language {
      implicit val format: OFormat[Language] = Json.format[Language]

      case class AppLevelLabels(navTitle:String, phaseBannerHtml: String)

      object AppLevelLabels {
        implicit val format: OFormat[AppLevelLabels] = Json.format[AppLevelLabels]
      }

      case class SelectPageLabels(title:String, heading: String, submitLabel: String, editAddressLinkText: String)

      object SelectPageLabels {
        implicit val format: OFormat[SelectPageLabels] = Json.format[SelectPageLabels]
      }

      case class LookupPageLabels(title:String, heading: String, filterLabel: String, postcodeLabel: String, submitLabel: String)

      object LookupPageLabels {
        implicit val format: OFormat[LookupPageLabels] = Json.format[LookupPageLabels]
      }

      case class ConfirmPageLabels(title:String,heading:String,showConfirmChangeText:Boolean)

      object ConfirmPageLabels {
        implicit val format: OFormat[ConfirmPageLabels] = Json.format[ConfirmPageLabels]
      }

      case class EditPageLabels(submitLabel:String)

      object EditPageLabels {
        implicit val format: OFormat[EditPageLabels] = Json.format[EditPageLabels]
      }

    }

  }

}
