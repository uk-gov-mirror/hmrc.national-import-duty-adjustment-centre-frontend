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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.bars

case class BARSResult(accountNumberWithSortCodeIsValid: String,
                      nonStandardAccountDetailsRequiredForBacs: Option[String],
                      supportsBACS: Option[String]) {

  val validAccountAndSortCode: Boolean = accountNumberWithSortCodeIsValid == "yes"

  val rollNotRequired: Boolean = nonStandardAccountDetailsRequiredForBacs match {
    case Some(answer) => answer == "no"
    case None => false
  }

  val accountSupportsBacs: Boolean = supportsBACS match {
    case Some(answer) => answer == "yes"
    case None => false;
  }

  val isValid: Boolean = validAccountAndSortCode && rollNotRequired && accountSupportsBacs
}

object BARSResult {

  def apply(validateResponse: ValidateBankDetailsResponse): BARSResult = new BARSResult(
    validateResponse.accountNumberWithSortCodeIsValid,
    validateResponse.nonStandardAccountDetailsRequiredForBacs,
    validateResponse.supportsBACS
  )

}
