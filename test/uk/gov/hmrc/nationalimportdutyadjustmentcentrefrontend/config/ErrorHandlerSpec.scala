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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.config

import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.UnitViewSpec
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.views.html.ErrorTemplate

class ErrorHandlerSpec extends UnitViewSpec {

  val errorPage: ErrorTemplate = instanceOf[ErrorTemplate]
  val appConfig: AppConfig     = instanceOf[AppConfig]

  val errorHandler = new ErrorHandler(errorPage, realMessagesApi)(appConfig)

  "ErrorHandler" should {

    "render standardErrorTemplate" in {

      val result = errorHandler.standardErrorTemplate("some title", "some heading", "some message")(request).body

      result must include("some title")
      result must include("some heading")
      result must include("some message")
    }
  }
}
