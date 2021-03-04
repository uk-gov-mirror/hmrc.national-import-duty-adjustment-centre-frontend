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

package uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.eis

import play.api.libs.json.Json
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.base.{JsonSchemaValidation, TestData, UnitSpec}
import uk.gov.hmrc.nationalimportdutyadjustmentcentrefrontend.models.amend._

import java.util.UUID

class EISAmendCaseRequestValidationSpec extends UnitSpec with JsonSchemaValidation with TestData {

  private val jsonSchema = loadSchema("/pega-update-case-spec/RequestJSONSchemaV0.1.JSON")

  "EISAmendCaseRequest" should {
    "validate a claim number" in {
      validationErrors(
        eisRequest(completeAmendAnswers.copy(caseReference = Some(CaseReference("NID12345"))))
      ) mustBe None
    }

    "fail a claim number if too short" in {
      validationErrors(eisRequest(completeAmendAnswers.copy(caseReference = Some(CaseReference("1"))))).size mustBe 1
    }

    "fail a claim number if too long" in {
      validationErrors(
        eisRequest(completeAmendAnswers.copy(caseReference = Some(CaseReference("1" * 65))))
      ).size mustBe 1
    }

    "validate a description claim number" in {
      validationErrors(
        eisRequest(completeAmendAnswers.copy(furtherInformation = Some(FurtherInformation("More Information"))))
      )
    }

    "fails a description when too long" in {
      validationErrors(
        eisRequest(completeAmendAnswers.copy(furtherInformation = Some(FurtherInformation("M" * 1025))))
      ).size mustBe 1
    }
  }

  private def eisRequest(answers: AmendAnswers) =
    EISAmendCaseRequest(
      AcknowledgementReference = UUID.randomUUID().toString.replace("-", "").takeRight(32),
      ApplicationType = "NIDAC",
      OriginatingSystem = "Digital",
      Content = EISAmendCaseRequest.Content(AmendClaim(answers))
    )

  private def validationErrors(eisRequest: EISAmendCaseRequest) =
    validateJsonAgainstSchema(Json.toJson(eisRequest), jsonSchema)

}
