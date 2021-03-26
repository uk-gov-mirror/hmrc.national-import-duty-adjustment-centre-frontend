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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.bars.{
  AddressLookupResponse,
  BARSResult
}

trait BarsTestData extends TestData {

  // Assess
  val validAssessResponse: AddressLookupResponse =
    AddressLookupResponse("yes", "yes", "no", "yes", "yes", "yes")

  val invalidSortcodeDoesNotExistResponse: AddressLookupResponse =
    AddressLookupResponse("no", "no", "no", "indeterminate", "indeterminate", "error")

  val invalidAccountNumberAssessResponse: AddressLookupResponse =
    AddressLookupResponse("yes", "no", "no", "yes", "yes", "indeterminate")

  val invalidNonStandardAccountNumberAssessResponse: AddressLookupResponse =
    AddressLookupResponse("yes", "yes", "yes", "yes", "yes", "yes")

  val invalidNoDirectCreditSupportResponse: AddressLookupResponse =
    AddressLookupResponse("yes", "yes", "no", "yes", "yes", "no")

  // Bars
  val barsSuccessResult: BARSResult = BARSResult(validAssessResponse)

  val barsSortcodeDoesNotExistResult: BARSResult = BARSResult(invalidSortcodeDoesNotExistResponse)

  val barsBacsNotSupportedResult: BARSResult = BARSResult(invalidNoDirectCreditSupportResponse)

  val barsInvalidAccountResult: BARSResult = BARSResult(invalidAccountNumberAssessResponse)

  val barsRollRequiredResult: BARSResult = BARSResult(invalidNonStandardAccountNumberAssessResponse)

}
