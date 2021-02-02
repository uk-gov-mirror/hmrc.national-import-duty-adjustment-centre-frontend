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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.forms.mappings

object Validation {

  val accountNumberPattern = "^[0-9]{6,8}$"
  val sortCodePattern      = "^[0-9]{6}$"
  val safeInputPattern     = """^[A-Za-z0-9À-ÿ \!\)\(.,_/’'"&-]+$"""
  val entryProcessingUnit  = "^[0-9]{3}$"
  val entryNumber          = "^([0-9]{6}[a-z|A-Z])$"

  val emailAddressPattern =
    """^[a-zA-Z0-9\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,85}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,85}[a-zA-Z0-9])?)*$"""

  val phoneNumberPattern = "^[0-9]{11}$"
  val postcodePattern    = "^[0-9a-zA-Z]{1,7}$"
}
