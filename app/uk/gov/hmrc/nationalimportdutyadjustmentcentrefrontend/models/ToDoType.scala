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

import play.api.data.Form
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.govukfrontend.views.viewmodels.hint.Hint
import uk.gov.hmrc.govukfrontend.views.viewmodels.radios.RadioItem
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.viewmodels.MessageKey

sealed trait ToDoType

object ToDoType extends Enumerable.Implicits {

  case object NewClaim   extends WithValue("New Claim") with ToDoType
  case object AmendClaim extends WithValue("Amend Claim") with ToDoType

  val values: Seq[ToDoType] = Seq(NewClaim, AmendClaim)

  def options(form: Form[_], name: String)(implicit messages: Messages): Seq[RadioItem] = values.map {
    value =>
      RadioItem(
        value = Some(value.toString),
        content = Text(MessageKey.apply("what_do_you_want_to_do", value.toString)),
        hint = Some(Hint(content = Text(MessageKey.apply("what_do_you_want_to_do.hint", value.toString)))),
        checked = form(name).value.contains(value.toString)
      )
  }

  implicit val enumerable: Enumerable[ToDoType] =
    Enumerable(values.map(v => v.toString -> v): _*)

}
