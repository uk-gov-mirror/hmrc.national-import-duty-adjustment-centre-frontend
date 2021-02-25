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

import org.scalacheck.Gen
import play.api.data.FormError
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.behaviours.StringFieldBehaviours
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings.Validation

class ContactDetailsFormProviderSpec extends StringFieldBehaviours {

  val form = new ContactDetailsFormProvider()()

  ".FirstName" must {

    val fieldName   = "firstName"
    val requiredKey = "contactDetails.firstName.error.required"
    val lengthKey   = "contactDetails.firstName.error.length"
    val maxLength   = 40

    behave like fieldThatBindsValidData(form, fieldName, safeInputsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".LastName" must {

    val fieldName   = "lastName"
    val requiredKey = "contactDetails.lastName.error.required"
    val lengthKey   = "contactDetails.lastName.error.length"
    val maxLength   = 40

    behave like fieldThatBindsValidData(form, fieldName, safeInputsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".EmailAddress" must {

    val fieldName   = "emailAddress"
    val requiredKey = "contactDetails.emailAddress.error.required"
    val invalidKey  = "contactDetails.emailAddress.error.invalid"
    val lengthKey   = "contactDetails.emailAddress.error.length"
    val maxLength   = 85

    val basicEmail            = Gen.const("foo@example.com")
    val emailWithSpecialChars = Gen.const("aBcD.!#$%&'*+/=?^_`{|}~-123@foo-bar.example.com")
    val validData             = Gen.oneOf(basicEmail, emailWithSpecialChars)

    behave like fieldThatBindsValidData(form, fieldName, validData)

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

  }

  ".TelephoneNumber" must {

    val fieldName   = "telephoneNumber"
    val requiredKey = "contactDetails.telephoneNumber.error.required"
    val invalidKey  = "contactDetails.telephoneNumber.error.invalid"
    val minLength   = 11
    val maxLength   = 11

    val validTelephoneNumberGen = for {
      length <- Gen.choose(minLength, maxLength)
      digits <- Gen.listOfN(length, Gen.numChar)
    } yield digits.mkString

    behave like fieldThatBindsValidData(form, fieldName, validTelephoneNumberGen)

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, invalidKey, Seq(Validation.phoneNumberPattern))
    )

  }
}
