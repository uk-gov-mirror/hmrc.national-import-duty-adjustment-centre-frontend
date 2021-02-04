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
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.Text
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.viewmodels.MessageKey

sealed trait ReclaimDutyType

object ReclaimDutyType extends Enumerable.Implicits {

  case object Customs extends WithValue("01") with ReclaimDutyType
  case object Vat     extends WithValue("02") with ReclaimDutyType
  case object Other   extends WithValue("03") with ReclaimDutyType

  val values: Seq[ReclaimDutyType] = Seq(Customs, Vat, Other)

  def options(form: Form[_], name: String)(implicit messages: Messages): Seq[CheckboxItem] = values.map {
    value =>
      CheckboxItem(
        name = Some(name),
        value = value.toString,
        content = Text(MessageKey.apply("reclaimDutyType", value.toString)),
        checked = form.data.exists(_._2 == value.toString)
      )
  }

  implicit val enumerable: Enumerable[ReclaimDutyType] =
    Enumerable(values.map(v => v.toString -> v): _*)

  implicit def typeToString: ReclaimDutyType => String = _.toString
}
