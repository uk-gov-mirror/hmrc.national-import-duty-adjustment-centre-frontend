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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings.{Mappings, Validation}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.{Address, ImporterContactDetails}

class ImporterDetailsFormProvider @Inject() extends Mappings {

  def apply(): Form[ImporterContactDetails] = Form(
    mapping(
      "name" -> text("address.name.error.required")
        .verifying(firstError(maxLength(40, "address.name.error.length"))),
      "addressLine1" -> text("address.line1.error.required")
        .verifying(firstError(maxLength(100, "address.line1.error.length"))),
      "addressLine2" -> optional(
        text()
          .verifying(firstError(maxLength(50, "address.line2.error.length")))
      ),
      "city" -> text("address.city.error.required")
        .verifying(firstError(maxLength(50, "address.city.error.length"))),
      "postcode" -> text("address.postcode.error.required")
        .verifying(
          firstError(
            maxLength(7, "address.postcode.error.length"),
            regexp(Validation.postcodePattern, "address.postcode.error.invalid")
          )
        ),
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
            firstError(regexp(Validation.phoneNumberPattern.toString, "contactDetails.telephoneNumber.error.invalid"))
          )
    )(ImporterContactDetails.apply)(ImporterContactDetails.unapply)
  )

}
