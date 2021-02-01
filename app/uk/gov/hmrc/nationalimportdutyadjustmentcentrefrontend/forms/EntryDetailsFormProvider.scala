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

import java.time.LocalDate

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings.{Mappings, Validation}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.EntryDetails
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.viewmodels.DateFormatter._

class EntryDetailsFormProvider @Inject() extends Mappings {

  def apply(): Form[EntryDetails] = {

    val earliestDate = LocalDate.parse("2000-01-01")

    Form(
      mapping(
        "entryProcessingUnit" -> text("entryDetails.claimEpu.error.required")
          .verifying(regexp(Validation.entryProcessingUnit, "entryDetails.claimEpu.error.invalid")),
        "entryNumber" -> text("entryDetails.entryNumber.error.required")
          .verifying(regexp(Validation.entryNumber, "entryDetails.entryNumber.error.invalid")),
        "entryDate" -> localDate(
          invalidKey = "entryDetails.claimEntryDate.error.invalid",
          requiredKey = "entryDetails.claimEntryDate.error.required"
        ).verifying(maxDate(LocalDate.now, "entryDetails.claimEntryDate.error.maxDate", format(LocalDate.now)))
          .verifying(minDate(earliestDate, "entryDetails.claimEntryDate.error.minDate", format(earliestDate)))
      )(EntryDetails.apply)(EntryDetails.unapply)
    )
  }

}
