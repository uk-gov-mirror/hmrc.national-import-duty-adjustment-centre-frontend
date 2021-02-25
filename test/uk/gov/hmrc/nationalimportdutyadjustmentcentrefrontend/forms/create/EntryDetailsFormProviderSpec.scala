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

import java.time.LocalDate

import play.api.data.FormError
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.behaviours.StringFieldBehaviours
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings.Validation
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.create.EntryDetails

class EntryDetailsFormProviderSpec extends StringFieldBehaviours {

  val form = new EntryDetailsFormProvider()()

  def buildFormData(
    epu: Option[String] = Some("123"),
    entryNumber: Option[String] = Some("123456Q"),
    day: Option[String] = Some("31"),
    month: Option[String] = Some("12"),
    year: Option[String] = Some("2020")
  ): Map[String, String] =
    (
      epu.map(_ => "entryProcessingUnit" -> epu.get) ++
        entryNumber.map(_ => "entryNumber" -> entryNumber.get) ++
        day.map(_ => "entryDate" -> day.get) ++
        month.map(_ => "entryDate.month" -> month.get) ++
        year.map(_ => "entryDate.year" -> year.get)
    ).toMap

  "Form" must {

    "Accept valid form data" in {
      val form2 = new EntryDetailsFormProvider().apply().bind(buildFormData())

      form2.hasErrors mustBe false
      form2.value mustBe Some(EntryDetails("123", "123456Q", LocalDate.of(2020, 12, 31)))
    }

  }

  ".EPU" must {

    val fieldName   = "entryProcessingUnit"
    val requiredKey = "entryDetails.claimEpu.error.required"
    val lengthKey   = "entryDetails.claimEpu.error.invalid"
    val maxLength   = 3

    behave like fieldThatBindsValidData(form, fieldName, Validation.entryProcessingUnit)

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(Validation.entryProcessingUnit))
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))
  }

  ".EntryNumber" must {

    val fieldName   = "entryNumber"
    val requiredKey = "entryDetails.entryNumber.error.required"
    val lengthKey   = "entryDetails.entryNumber.error.invalid"
    val maxLength   = 7

    behave like fieldThatBindsValidData(form, fieldName, entryNumber())

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(Validation.entryNumber))
    )

    behave like mandatoryField(form, fieldName, requiredError = FormError(fieldName, requiredKey))

    "fail to bind entries that do not contain 6 digits and a letter" in {
      val fieldName = "entryNumber"
      val lengthKey = "entryDetails.entryNumber.error.invalid"

      val result        = form.bind(Map(fieldName -> "12345678AQ")).apply(fieldName)
      val expectedError = FormError(fieldName, lengthKey, Seq(Validation.entryNumber))
      result.errors mustEqual Seq(expectedError)
    }
  }

  ".EntryDate" must {

    "Fail if the Date has a future day" in {
      val futureDate = LocalDate.now.plusDays(1)
      val form2 = new EntryDetailsFormProvider().apply().bind(
        buildFormData(
          day = Some(s"${futureDate.getDayOfMonth}"),
          month = Some(s"${futureDate.getMonthValue}"),
          year = Some(s"${futureDate.getYear}")
        )
      )

      form2.errors.size mustBe 1
      form2.errors.head.key mustBe "entryDate"
      form2.errors.head.message mustBe "entryDetails.claimEntryDate.error.maxDate"
    }

    "Fail if the Date is too early" in {
      val form2 = new EntryDetailsFormProvider().apply().bind(buildFormData(year = Some("1800")))

      form2.errors.size mustBe 1
      form2.errors.head.key mustBe "entryDate"
      form2.errors.head.message mustBe "entryDetails.claimEntryDate.error.minDate"
    }

    "Fail if the Date contains an invalid date" in {
      val form2 = new EntryDetailsFormProvider().apply().bind(buildFormData(day = Some("31"), month = Some("2")))

      form2.errors.size mustBe 1
      form2.errors.head.key mustBe "entryDate"
      form2.errors.head.message mustBe "entryDetails.claimEntryDate.error.invalid"
    }

  }
}
