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

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings.{Mappings, Validation}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.ContactDetails

class ContactDetailsFormProvider @Inject() extends Mappings {

  def apply(): Form[ContactDetails] = Form(
    mapping(
      "firstName" -> text("contactDetails.firstName.error.required")
        .verifying(firstError(maxLength(40, "contactDetails.firstName.error.length"))),
      "lastName" -> text("contactDetails.lastName.error.required")
        .verifying(firstError(maxLength(40, "contactDetails.lastName.error.length"))),
      "emailAddress" -> text("contactDetails.emailAddress.error.required")
        .verifying(
          firstError(
            maxLength(85, "contactDetails.emailAddress.error.length"),
            regexp(Validation.emailAddressPattern.toString, "contactDetails.emailAddress.error.invalid")
          )
        ),
      "telephoneNumber" ->
        text("contactDetails.telephoneNumber.error.required")
          .verifying(
            firstError(
              minLength(9, "contactDetails.telephoneNumber.error.length"),
              maxLength(32, "contactDetails.telephoneNumber.error.length"))
          )
    )(ContactDetails.apply)(ContactDetails.unapply)
  )

}
