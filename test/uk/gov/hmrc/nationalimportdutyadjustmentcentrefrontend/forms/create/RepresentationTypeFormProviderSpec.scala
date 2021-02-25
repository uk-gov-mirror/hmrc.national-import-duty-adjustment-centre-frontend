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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.create

import play.api.data.FormError
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.behaviours.OptionFieldBehaviours
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.RepresentationType

class RepresentationTypeFormProviderSpec extends OptionFieldBehaviours {

  val form = new RepresentationTypeFormProvider()()

  "RepresentationTypeFormProvider" must {

    val fieldName   = "representation_type"
    val requiredKey = "representation_type.error.required"

    behave like optionsField[RepresentationType](
      form,
      fieldName,
      validValues = RepresentationType.values,
      invalidError = FormError(fieldName, "error.invalid")
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }
}
