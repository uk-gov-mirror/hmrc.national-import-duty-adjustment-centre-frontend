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

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{Enumerable, WithValue}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.viewmodels.MessageKey

sealed trait ClaimType

object ClaimType extends Enumerable.Implicits {

  case object Airworthiness extends WithValue("Airworthiness") with ClaimType
  case object AntiDumping   extends WithValue("Anti-Dumping") with ClaimType
  case object Preference    extends WithValue("Preference") with ClaimType
  case object Quota         extends WithValue("Quota") with ClaimType

  val values: Seq[ClaimType] = Seq(Airworthiness, AntiDumping, Preference, Quota)

  def options(form: Form[_], name: String)(implicit messages: Messages): Seq[RadioItem] = values.map {
    value =>
      RadioItem(
        value = Some(value.toString),
        content = Text(MessageKey.apply("claim_type", value.toString)),
        checked = form(name).value.contains(value.toString)
      )
  }

  implicit val enumerable: Enumerable[ClaimType] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
