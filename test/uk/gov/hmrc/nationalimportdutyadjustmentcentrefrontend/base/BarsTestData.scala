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
  AssessBusinessBankDetailsResponse,
  BARSResult,
  MetadataResponse
}

trait BarsTestData extends TestData {

  // Metadata
  val validMetaDataResponse: MetadataResponse  = MetadataResponse("M")
  val noBacsMetaDataResponse: MetadataResponse = MetadataResponse("N")

  // Assess
  val validAssessResponse: AssessBusinessBankDetailsResponse =
    AssessBusinessBankDetailsResponse("yes", "no", "yes", "yes")

  val invalidAccountNumberAssessResponse: AssessBusinessBankDetailsResponse =
    AssessBusinessBankDetailsResponse("no", "no", "yes", "yes")

  val invalidNonStandardAccountNumberAssessResponse: AssessBusinessBankDetailsResponse =
    AssessBusinessBankDetailsResponse("yes", "yes", "yes", "yes")

  // Bars
  val barsSuccessResult: BARSResult = BARSResult(validMetaDataResponse, validAssessResponse)

  val barsBacsNotSupportedResult: BARSResult =
    BARSResult(noBacsMetaDataResponse, AssessBusinessBankDetailsResponse.notApplicable)

  val barsInvalidAccountResult: BARSResult = BARSResult(validMetaDataResponse, invalidAccountNumberAssessResponse)

  val barsRollRequiredResult: BARSResult =
    BARSResult(validMetaDataResponse, invalidNonStandardAccountNumberAssessResponse)

}
