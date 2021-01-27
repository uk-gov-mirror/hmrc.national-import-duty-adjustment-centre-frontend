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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.navigation

import javax.inject.{Inject, Singleton}
import play.api.mvc.Call
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.controllers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.UserAnswers
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{
  BankDetailsPage,
  ClaimTypePage,
  Page,
  ReclaimDutyTypePage,
  UploadPage
}

@Singleton
class Navigator @Inject() () {

  private val normalRoutes: (Page, UserAnswers) => Call = {
    case (ClaimTypePage, _)       => controllers.makeclaim.routes.UploadFormController.onPageLoad()
    case (UploadPage, _)          => controllers.makeclaim.routes.ReclaimDutyTypeController.onPageLoad()
    case (ReclaimDutyTypePage, _) => controllers.makeclaim.routes.BankDetailsController.onPageLoad()
    case (BankDetailsPage, _)     => controllers.makeclaim.routes.CheckYourAnswersController.onPageLoad()
    case _                        => controllers.routes.StartController.start()
  }

  def nextPage(page: Page, userAnswers: UserAnswers): Call =
    normalRoutes(page, userAnswers)

}
