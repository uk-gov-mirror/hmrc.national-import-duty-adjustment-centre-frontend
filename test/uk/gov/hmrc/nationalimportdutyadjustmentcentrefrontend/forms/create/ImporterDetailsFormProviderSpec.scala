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

class ImporterDetailsFormProviderSpec extends StringFieldBehaviours {

  val form = new ImporterDetailsFormProvider()()

  ".Name" must {

    val fieldName   = "name"
    val requiredKey = "address.name.error.required"
    val lengthKey   = "address.name.error.length"
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

  ".AddressLine1" must {

    val fieldName   = "addressLine1"
    val requiredKey = "address.line1.error.required"
    val lengthKey   = "address.line1.error.length"
    val maxLength   = 100

    behave like fieldThatBindsValidData(form, fieldName, safeInputsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".AddressLine2" must {

    val fieldName = "addressLine2"
    val lengthKey = "address.line2.error.length"
    val maxLength = 50

    behave like fieldThatBindsValidData(form, fieldName, safeInputsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like optionalField(form, fieldName)
  }

  ".City" must {

    val fieldName   = "city"
    val requiredKey = "address.city.error.required"
    val lengthKey   = "address.city.error.length"
    val maxLength   = 50

    behave like fieldThatBindsValidData(form, fieldName, safeInputsWithMaxLength(maxLength))

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".Postcode" must {

    val fieldName   = "postcode"
    val requiredKey = "address.postcode.error.required"
    val lengthKey   = "address.postcode.error.length"
    val invalidKey  = "address.postcode.error.invalid"

    val validPostCodeGen = for {
      leading       <- Gen.listOfN(10, " ").map(_.mkString)
      firstGroup    <- Gen.listOfN(3, Gen.numChar).map(_.toString)
      internalSpace <- Gen.listOfN(10, " ").map(_.mkString)
      secondGroup   <- Gen.listOfN(4, Gen.numChar).map(_.toString)
      trailing      <- Gen.listOfN(15, " ").map(_.mkString)
    } yield s"$leading$firstGroup$internalSpace$secondGroup$trailing"

    behave like fieldThatBindsValidData(form, fieldName, validPostCodeGen)

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "not bind strings with invalid characters" in {
      val result        = form.bind(Map(fieldName -> "P!24KF")).apply(fieldName)
      val expectedError = FormError(fieldName, invalidKey)
      result.errors mustEqual Seq(expectedError)
    }

    "not bind empty string" in {
      val result        = form.bind(Map(fieldName -> "       ")).apply(fieldName)
      val expectedError = FormError(fieldName, lengthKey)
      result.errors mustEqual Seq(expectedError)
    }

    "not bind postcode with too many characters postcleansing" in {
      val result        = form.bind(Map(fieldName -> "PLACE42 23REGION")).apply(fieldName)
      val expectedError = FormError(fieldName, lengthKey)
      result.errors mustEqual Seq(expectedError)
    }

    "not bind postcode with too few characters postcleansing" in {
      val result        = form.bind(Map(fieldName -> "     A5      X      ")).apply(fieldName)
      val expectedError = FormError(fieldName, lengthKey)
      result.errors mustEqual Seq(expectedError)
    }
  }

}
