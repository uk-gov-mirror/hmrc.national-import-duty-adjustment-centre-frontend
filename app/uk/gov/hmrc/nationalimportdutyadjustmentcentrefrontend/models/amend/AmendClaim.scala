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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend

import play.api.Logger
import play.api.libs.json.{Json, OFormat}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.exceptions.MissingAnswersException
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.pages.{CaseReferencePage, FurtherInformationPage}

final case class AmendClaim(caseReference: CaseReference, furtherInformation: FurtherInformation)

object AmendClaim {
  implicit val formats: OFormat[AmendClaim] = Json.format[AmendClaim]

  def apply(answers: AmendAnswers): AmendClaim = new AmendClaim(
    caseReference = answers.caseReference.getOrElse(missing(CaseReferencePage)),
    furtherInformation = answers.furtherInformation.getOrElse(missing(FurtherInformationPage))
  )

  private def missing(answer: Any) = {
    val message = s"Missing answer - $answer"
    Logger(this.getClass).warn(message)
    throw new MissingAnswersException(message)
  }

}
