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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models

import java.util.UUID

import play.api.libs.json.{Json, OFormat}
import play.api.mvc.QueryStringBindable

case class JourneyId(value: String)

object JourneyId {
  def generate: JourneyId = JourneyId(UUID.randomUUID().toString)

  implicit val idFormat: OFormat[JourneyId] = Json.format[JourneyId]

  implicit def queryBinder(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[JourneyId] =
    stringBinder.transform(JourneyId(_), _.value)

}
