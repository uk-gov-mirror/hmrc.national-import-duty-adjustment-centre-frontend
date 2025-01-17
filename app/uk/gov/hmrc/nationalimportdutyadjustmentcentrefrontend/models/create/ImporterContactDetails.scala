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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create

import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.Implicits.SanitizedString

case class ImporterContactDetails(
  addressLine1: String,
  addressLine2: Option[String],
  addressLine3: Option[String],
  city: String,
  postCode: String
)

object ImporterContactDetails {
  implicit val format: OFormat[ImporterContactDetails] = Json.format[ImporterContactDetails]

  def apply(
    addressLine1: String,
    addressLine2: Option[String],
    addressLine3: Option[String],
    city: String,
    postCode: String
  ): ImporterContactDetails =
    new ImporterContactDetails(
      addressLine1,
      addressLine2,
      addressLine3,
      city,
      postCode.stripExternalAndReduceInternalSpaces()
    )

}
